using Backend.DTOs;
using Backend.Enums;
using Backend.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;

namespace Backend.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class BooksController : ControllerBase
    {
        private readonly IHttpClientFactory _httpClientFactory;
        private readonly IWebHostEnvironment _env;
        private readonly AppDbContext _context;

        public BooksController(AppDbContext context, IHttpClientFactory httpClientFactory, IWebHostEnvironment env)
        {
            _httpClientFactory = httpClientFactory;
            _context = context;
            _env = env;
        }
        [HttpGet("Search-By-Isbn/{isbn}")]
        public async Task<ActionResult<Book>> GetBookByIsbn(string isbn)
        {
            var book = await _context.Books.FirstOrDefaultAsync(b => b.Isbn == isbn);
            if (book != null) return Ok(book);

            var apiKey = Environment.GetEnvironmentVariable("API_KEY");

            if (string.IsNullOrEmpty(apiKey))
                return StatusCode(500, "API ključ nije podešen.");

            var client = _httpClientFactory.CreateClient();
            var url = $"https://www.googleapis.com/books/v1/volumes?q=isbn:{isbn}&key={apiKey}";
            
            var response = await client.GetFromJsonAsync<GoogleBooksResponse>(url);

            var item = response?.Items?.FirstOrDefault();
            if (item == null) return NotFound("Knjiga nije pronađena.");

            var newBook = new Book
            {
                Title = item.VolumeInfo.Title,
                Author = item.VolumeInfo.Authors?.FirstOrDefault() ?? "Nepoznat autor",
                Isbn = isbn,
                PageCount = item.VolumeInfo.PageCount ?? 0,
                Description = item.VolumeInfo.Description,
                Status = BookStatus.Unread
            };

            _context.Books.Add(newBook);
            await _context.SaveChangesAsync();

            return Ok(newBook);
        }

        [HttpGet("Get-Books")]
        public async Task<ActionResult<IEnumerable<Book>>> GetBooks()
        {
            return await _context.Books.ToListAsync();
        }

        [HttpPost("Add-Book")]
        public async Task<ActionResult<Book>> AddBook(BookCreateDto bookDto)
        {
            var book = new Book 
            {
                Title = bookDto.Title,
                Author = bookDto.Author,
                Isbn = bookDto.Isbn,
                Description = bookDto.Description,
                PageCount = bookDto.PageCount,
                CurrentPage = 0,
                Status = BookStatus.Unread
            };

            _context.Books.Add(book);
            await _context.SaveChangesAsync();

            return CreatedAtAction(nameof(GetBook), new { id = book.Id }, book);
        }

        [HttpGet("Get-Book/{id}")]
        public async Task<ActionResult<Book>> GetBook(int id)
        {
            var book = await _context.Books.FindAsync(id);

            if (book == null) return NotFound();

            return book;
        }
        [HttpPatch("Update-Progress/{id}")]
        public async Task<IActionResult> UpdateProgress(int id, [FromBody] int currentPage)
        {
            var book = await _context.Books.FindAsync(id);
            if (book == null) return NotFound();

            
            if (currentPage >= book.PageCount)
            {
                book.CurrentPage = book.PageCount;
                book.Status = BookStatus.Finished;
            }
            else if (currentPage > 0)
            {
                book.CurrentPage = currentPage;
                book.Status = BookStatus.Reading;
            }
            else
            {
                book.Status = BookStatus.Unread;
            }

            await _context.SaveChangesAsync();
            return NoContent();
        }
        [HttpPatch("Update-Status/{id}")]
        public async Task<IActionResult> UpdateStatus(int id, [FromBody] BookStatus newStatus)
        {
            var book = await _context.Books.FindAsync(id);
            if (book == null) return NotFound();

            book.Status = newStatus;
            await _context.SaveChangesAsync();
            return NoContent();
        }
        [HttpDelete("Delete-Book/{id}")]
        public async Task<IActionResult> DeleteBook(int id)
        {
            var book = await _context.Books.FindAsync(id);
            if (book == null)
            {
                return NotFound();
            }

            _context.Books.Remove(book);
            await _context.SaveChangesAsync();

            return NoContent();
        }
        
        [HttpPost("Upload-Cover/{id}")]
        public async Task<IActionResult> UploadCover(int id, IFormFile file)
        {
            var book = await _context.Books.FindAsync(id);
            if (book == null) return NotFound("Knjiga nije pronađena.");

            var uploadFolder = Path.Combine(_env.WebRootPath, "uploads", "covers");
            if (!Directory.Exists(uploadFolder)) Directory.CreateDirectory(uploadFolder);

            var fileName = Guid.NewGuid().ToString() + Path.GetExtension(file.FileName);
            var filePath = Path.Combine(uploadFolder, fileName);

            using (var stream = new FileStream(filePath, FileMode.Create))
            {
                await file.CopyToAsync(stream);
            }

            book.ImagePath = fileName;
            await _context.SaveChangesAsync();

            return Ok(new { fileName });
        }
    }
    public class GoogleBooksResponse 
    { 
        public List<BookItem>? Items { get; set; } 
    }

    public class BookItem 
    { 
        public VolumeInfo VolumeInfo { get; set; } = null!; 
    }

    public class VolumeInfo 
    { 
        public string Title { get; set; } = ""; 
        public List<string>? Authors { get; set; } 
        public int? PageCount { get; set; }
        public string? Description { get; set; }
    }
}