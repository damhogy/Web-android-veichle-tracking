using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace Easy_Track_MVC.Models
{
    [MetadataType(typeof(CustomStudent))]

    public partial class Student
    { }

    public class CustomStudent
    {
        public int Id { get; set; }
        [Required(ErrorMessage = "Please Choose Parent ID")]
        public Nullable<int> P_Id { get; set; }
        [Required(ErrorMessage = "Please Choose Bus Num")]
        public Nullable<int> B_Id { get; set; }
        [DisplayName("Student Name")]
        [Required(ErrorMessage = "Please Enter Name")]
        [MinLength(3, ErrorMessage = "Min 3")]
        public string Name { get; set; }
        [Required(ErrorMessage = "Please Enter Age")]
        public Nullable<int> Age { get; set; }
        [Required(ErrorMessage = "Please Enter Class")]
        public string Class { get; set; }

        public virtual Bus Bus { get; set; }
        public virtual Parent Parent { get; set; }

    }
}