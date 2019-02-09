using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Demo.Models;
using System.Net;

//phar_id =1; central

namespace Demo.Controllers
{
    public class DemandController : Controller
    {
        //
        // GET: /demand/
        private db_9ff70c_centralEntities central = new db_9ff70c_centralEntities();
        private TempEntities temp = new TempEntities();

        public ActionResult Demand(int ?id )
        {
            if (ConfirmLogIn() != true)
                return RedirectToAction("Login", "Account");
            if (id == null)
            {
                return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
            }
            var med = central.warehousemedicines.Find(id);
            if (med == null)
            {
                return HttpNotFound();
            }
            return View(med);
        }
        [HttpPost]
        public ActionResult Demand(int id,int ? NewQuantity)
        {
            if (ConfirmLogIn() != true)
                return RedirectToAction("Login", "Account");
            if (NewQuantity <= 0)
                return HttpNotFound();
            var med = central.warehousemedicines.Find(id);
            demand de = new demand();
            de.Name = med.Name;
            de.Price = med.Price;
            de.ExpiredDate = med.Expired_date;
            de.Status = false;
            de.Phar_id = 1;
            de.warhouse_id = med.Warehouse_id;
            de.Quantity = (int)NewQuantity;
            de.companyName = med.CompanyName;
            de.chemicalForm = med.ChemicalForm;
            de.Date_submitted = DateTime.Now;
            central.demands.Add(de);
            central.SaveChanges();
            var te = new temp();
            te.Name = de.Name;
            te.Price = de.Price;
            te.Quantity = de.Quantity;
            te.Phar_id = de.Phar_id;
            te.Demand = true;
            temp.temps.Add(te);
            temp.SaveChanges();
            return RedirectToAction("DemandsList");
        }
        public ActionResult DemandsList()
        {
            if (ConfirmLogIn() != true)
                return RedirectToAction("Login", "Account");
            var demand = from m in central.demands.Where(s=>s.Phar_id==1)
                         select m;     
            return View(demand);
        }

        public ActionResult DeleteDemand(int id)
        {
            if (ConfirmLogIn() != true)
                return RedirectToAction("Login", "Account");

            var demand = central.demands.Find(id);

            return View(demand);

        }
        [HttpPost, ActionName("DeleteDemand")]
        public ActionResult ConfirmDelete(int? id)
        {
            if (ConfirmLogIn() != true)
                return RedirectToAction("Login", "Account");

            demand demand = central.demands.Find(id);
            central.demands.Remove(demand);
            central.SaveChanges();
            return RedirectToAction("DemandsList", "Demand");
        }

        public bool ConfirmLogIn()
        {
            return (System.Web.HttpContext.Current.User != null) && System.Web.HttpContext.Current.User.Identity.IsAuthenticated;
        }
        public static bool CheckForInternetConnection()
        {
            try
            {
                using (var client = new WebClient())
                {
                    using (var stream = client.OpenRead("http://www.google.com"))
                    {
                        return true;
                    }
                }
            }
            catch
            {
                return false;
            }
        }

    }
}
