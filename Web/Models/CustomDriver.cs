using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace Easy_Track_MVC.Models
{
    [MetadataType(typeof(CustomDriver))]

    public partial class Driver
    { }

    public class CustomDriver
    {
        public int Id { get; set; }
        [Required(ErrorMessage = "Please Enter Bus Num")]
        public Nullable<int> B_Id { get; set; }
        [Required(ErrorMessage = "Please Enter Name")]
        [MinLength(3,ErrorMessage ="Min 3")]
        public string Name { get; set; }
        [Required(ErrorMessage = "Please Enter User Name")]
        [RegularExpression("(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{6,15})$", ErrorMessage ="Please enter user name in format like: name123")]
        public string UserName { get; set; }
        [Required(ErrorMessage = "Please Enter Email")]
        [EmailAddress(ErrorMessage ="Email is not valid")]
        public string Email { get; set; }
        [Required(ErrorMessage = "Please Enter Mobile")]
        [MinLength(11, ErrorMessage = "Enter Valid Phone Number")]
        public string Mobile { get; set; }
        [Required(ErrorMessage = "Please Enter Password")]
        [RegularExpression("(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{6,15})$",ErrorMessage ="Enter at least 6 character and Digit")]
        public string Password { get; set; }
        public virtual Bus Bus { get; set; }

    }
}