using System;
using System.Collections.Generic;
using System.Data;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Warehouse1.Models;

namespace Warehouse1.Controllers
{
    public class HomeController : Controller
    {

        private db_9ff70c_centralEntities central = new db_9ff70c_centralEntities();

        public ActionResult Index()
        {

            return View();
        }

        public ActionResult Demands()
        {
            var a = from m in central.demands.Where(s => s.warhouse_id == 1 && s.Status == false)
                    select m;

            ViewBag.smer = "Central";
            return View(a);
        }

        public ActionResult AcceptDemand(int? id)
        {
            var a = central.demands.Find(id);
            a.Status = true;
            foreach (warehousemedicine wa in central.warehousemedicines)
            {
                if (wa.Name == a.Name && wa.Price == a.Price && wa.Expired_date == a.ExpiredDate)
                {
                    wa.Quantity = wa.Quantity - a.Quantity;
                    central.Entry(wa).State = EntityState.Modified;
                }
            }
            central.SaveChanges();
            return RedirectToAction("Demands");
        }
        public ActionResult AcceptedDemands()
        {
            var a = central.demands.Where(s => s.Status == true && s.warhouse_id == 1);
            return View(a);
        }
        public ActionResult Medicines()
        {
            var a = central.warehousemedicines.Where(s => s.Warehouse_id == 1);
            return View(a);
        }
        public ActionResult CreateMed()
        {
            return View();
        }
        [HttpPost]
        public ActionResult CreateMed(warehousemedicine med)
        {
            central.warehousemedicines.Add(med);
            central.SaveChanges();

            return RedirectToAction("Medicines");
        }
        public ActionResult EditMed(int id)
        {
            var a = central.warehousemedicines.Find(id);

            return View(a);

        }
        [HttpPost]
        public ActionResult EditMed(warehousemedicine med)
        {
            central.Entry(med).State = EntityState.Modified;
            central.SaveChanges();
            return RedirectToAction("Medicines");

        }
        public ActionResult DeleteMed(int id)
        {
            var a = central.warehousemedicines.Find(id);
            return View(a);
        }
        [HttpPost,ActionName("DeleteMed")]
        public ActionResult DeleteConfirmed(int id)
        {
            var med = central.warehousemedicines.Find(id);
            central.warehousemedicines.Remove(med);
            central.SaveChanges();
            return RedirectToAction("Medicines");
        }
    }
}
