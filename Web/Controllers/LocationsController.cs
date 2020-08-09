using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Mvc;
using Easy_Track_MVC.Models;
using System.Data.SqlClient;
using System.Web.Configuration;

namespace Easy_Track_MVC.Controllers
{
    public class LocationsController : Controller
    {
        private ETSBEntities db = new ETSBEntities();


        public ActionResult Edit()
        {
            return View();
        }
        [HttpPost, ActionName("Edit")]
        [ValidateAntiForgeryToken]
        public ActionResult Edit(int id)
        {
            try
            {
                SqlDataAdapter da = new SqlDataAdapter();
                using (SqlConnection openconec = new SqlConnection(WebConfigurationManager.ConnectionStrings["ETSBEntities"].ConnectionString))
                {
                    string SaveStaff = "SELECT  [Latitued],[Longitued] FROM [Location] WHERE [DateRegister] = (select max ([DateRegister]) FROM [Location] )";
                    using (SqlCommand QuerySaveStaff = new SqlCommand(SaveStaff))
                    {
                        QuerySaveStaff.Connection = openconec;
                        DataSet ds = new DataSet();
                        openconec.Open();
                        DataTable table = new DataTable();
                        table.Load(QuerySaveStaff.ExecuteReader());
                        ds.Tables.Add(table);
                        Response.Redirect("https://www.google.com/maps/place/" + Convert.ToString(table.Rows[0]["Latitued"]) + "," + Convert.ToString(table.Rows[0]["Longitued"]));
                        openconec.Close();
                    }
                }

            }
            catch (Exception ex)
            {

                Response.Write(ex.Message);
            }
            return View();

        }

        
    }
}
