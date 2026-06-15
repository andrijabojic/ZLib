using System.ComponentModel.DataAnnotations;
using Backend.Enums;

namespace Backend.Models
{
    public class Book
    {
        [Key]
        public int Id { get; set; }

        public required string Title { get; set; }

        public required string Author { get; set; }
        public string? Isbn { get; set; }

        public string? Description { get; set; }
        public int PageCount { get; set; }

        public int CurrentPage { get; set; } = 0;
        public BookStatus Status { get; set; } = BookStatus.Unread;
        
        public int? Rating { get; set; }

        public string? ImagePath { get; set; }
        
    }
}