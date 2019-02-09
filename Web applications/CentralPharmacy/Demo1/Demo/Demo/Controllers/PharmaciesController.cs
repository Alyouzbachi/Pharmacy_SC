using System;
using System.Collections.Generic;
using System.Data;
using System.Data.Entity;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Demo.Models;

namespace Demo.Controllers
{
    public class PharmaciesController : Controller
    {
        private db_9ff70c_centralEntities central = new db_9ff70c_centralEntities();

        //
        // GET: /Pharmacies/

        public ActionResult showPharmacies()
        {
            var a = from m in central.pharmacies.Where(s=>s.id!=1) select m;
            
            return View(a.ToList());
        }
        public ActionResult ShowMedicines(int ? id)
        {
            var a = from m in central.meds.Where(s => s.Pharmacy_id == id) select m;

            ViewBag.pharmacyid = id;
            return View(a.ToList());
        }
       
       
    }
}