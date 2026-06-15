namespace Backend.DTOs
{
    public class BookCreateDto
    {
        public required string Title { get; set; }
        public required string Author { get; set; }
        public string? Isbn { get; set; }
        public string? Description { get; set; }
        public int PageCount { get; set; }
        public string? ImagePath { get; set; }
    }
}