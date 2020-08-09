using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace Easy_Track_MVC.Models
{
    [MetadataType(typeof(CustomAdmin))]

    public partial class Admin
    { }

    public class CustomAdmin
    {
        public int Id { get; set; }
        [Required(ErrorMessage = "Please Enter Bus UserName")]
        [RegularExpression("(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{6,15})$", ErrorMessage = "Please enter user name in format like: name123")]
        public string UserName { get; set; }
        [Required(ErrorMessage = "Please Enter Bus Password")]
        [RegularExpression("(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9]{6,15})$", ErrorMessage = "Enter at least 6 character and Digit")]
        public string Password { get; set; }

    }
}