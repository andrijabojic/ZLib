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
        private readonly AppDbContext _context;

        public BooksController(AppDbContext context)
        {
            _context = context;
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
    }
}