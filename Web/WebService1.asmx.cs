using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Data;
using System.Web.Services;
using System.Data.SqlClient;
using System.Web.Configuration;


namespace Easy_Track_MVC
{
    /// <summary>
    /// Summary description for WebService1
    /// </summary>
    [WebService(Namespace = "http://tempuri.org/")]
    [WebServiceBinding(ConformsTo = WsiProfiles.BasicProfile1_1)]
    [System.ComponentModel.ToolboxItem(false)]
    // To allow this Web Service to be called from script, using ASP.NET AJAX, uncomment the following line. 
    // [System.Web.Script.Services.ScriptService]
    public class WebService1 : System.Web.Services.WebService
    {

        [WebMethod(MessageName = "getinfo")]
        [System.Xml.Serialization.XmlInclude(typeof(ContactResult))]
        public string TrackPhone(string Log, string lat)
        {
            ContactResult cr = new ContactResult();
            
            try
            {
                using (SqlConnection openconec = new SqlConnection(WebConfigurationManager.ConnectionStrings["ETSBEntities"].ConnectionString))
                    
                {
                    string SaveLoc = "INSERT into Location(Latitued,Longitued,DateRegister) Values( @androidlog,@androidlat,@androiddateregister)";

                    using (SqlCommand QuerySaveLoc = new SqlCommand(SaveLoc))
                    {
                        QuerySaveLoc.Connection = openconec;
                        QuerySaveLoc.Parameters.Add("@androidlog", SqlDbType.Float).Value = Log;
                        QuerySaveLoc.Parameters.Add("@androidlat", SqlDbType.Float).Value = lat;
                        QuerySaveLoc.Parameters.Add("@androiddateregister", SqlDbType.DateTime).Value = DateTime.Now.ToString();
                        openconec.Open();
                        QuerySaveLoc.ExecuteNonQuery();
                        openconec.Close();

                    }
                }
                cr.ErrorId = 0;
                cr.ErrorMessage = "Location is Added";
                return cr.ErrorMessage;        
            }
            catch (Exception ex)
            {
                cr.ErrorId = 1;
                cr.ErrorMessage = ex.Message;
                return cr.ErrorMessage;
            }
        }
    }
}
