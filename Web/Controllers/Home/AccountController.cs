using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using System.Web.Security;
using System.Security;
using Easy_Track_MVC.Models;

namespace Easy_Track_MVC.Controllers.Home
{
    public class AccountController : Controller
    {
        ETSBEntities db = new ETSBEntities();

        // GET: Account
        public ActionResult LoginView()
        {
            return View();
        }
        

    }
}