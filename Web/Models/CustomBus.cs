using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace Easy_Track_MVC.Models
{
    [MetadataType(typeof(CustomBus))]

    public partial class Bus
    { }
    public class CustomBus
    {
        public int Id { get; set; }
        [Required(ErrorMessage ="Please Enter Bus Num")]
        public Nullable<int> BusNum { get; set; }
        [Required(ErrorMessage = "Please Enter Route Start")]
        public string RouteStart { get; set; }
        [Required(ErrorMessage = "Please Enter RouteEnd")]
        public string RouteEnd { get; set; }
        [DisplayName("Route On Map")]
        [Required(ErrorMessage = "Please Paste the Route Map Link here")]
        public string RouteMapLink { get; set; }
    }
}