using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Demo.Models;
using System.Web.Mvc.Ajax;
using System.Data;
using System.Net;
using System.Data.Objects;
using System.Data.Entity;
using System.Text;

//This is for Central Pharmacy ID=1
//CreateDelete in table temp 1 for Creating 0 for Deleting0


    namespace Demo.Controllers
    {
        public class HomeController : Controller
        {
            private PharmacyEntities db = new PharmacyEntities();
            private PharmacyEntities1 db1 = new PharmacyEntities1();
            private db_9ff70c_centralEntities central = new db_9ff70c_centralEntities();
            private TempEntities temp = new TempEntities();

            public ActionResult Index()
            {
              //  AddallMedicine();
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (CheckForInternetConnection() == false)
                    return View();
                else
                {
                    Refresh1();
                    return View("IndexConnection");
                }
            }

            public ActionResult Index1()
            {
                pharmacy a = central.pharmacies.Find(1);

                return View(a);
            }
            public ActionResult Medicine()
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (CheckForInternetConnection() == true)
                    Refresh1();
                return View(db.Medicines.ToList());
            }

            #region Create
            public ActionResult Create()
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");

                List<string> Names = new List<string>();
                foreach(Company a in db.Companies)
                {
                    Names.Add(a.Name);  
                }
                List<string> chemical = new List<string>();
                foreach (ChemicalForm a in db.ChemicalForms)
                {
                    chemical.Add(a.ChemicalForm1);
                }
                ViewBag.chemicalForms = chemical;
                ViewBag.CompanyNames = Names;

                return View();
            }

            [AcceptVerbs(HttpVerbs.Post)]

            public ActionResult Create([Bind(Exclude = "Id")] Medicine MedicineToadd, DateTime? date)
            {

                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (!ModelState.IsValid)

                    return View();
                string Name = Request.Form["Names"].ToString();
                string chem = Request.Form["Forms"].ToString();
                MedicineToadd.ChemicalForm = db.ChemicalForms.First(a => a.ChemicalForm1 == chem).Id;
                MedicineToadd.Company_ID = db.Companies.First(a => a.Name == Name).Id;
                MedicineToadd.Expire_Date = (DateTime)date;
                db.Medicines.Add(MedicineToadd);
                db.SaveChanges();
                if (CheckForInternetConnection() == true)
                {
                    med med = new med();
                    med.Price = (float)MedicineToadd.Price;
                    med.Name = MedicineToadd.Name;
                    med.Quantity = MedicineToadd.Quantity;
                    med.Pharmacy_id = 1;
                    central.meds.Add(med);
                    central.SaveChanges();
                }
                else
                {
                    temp a = new temp();
                    a.create = true;
                    a.Name = MedicineToadd.Name;
                    a.Price=MedicineToadd.Price;
                    a.Quantity = MedicineToadd.Quantity;
                    a.Phar_id = 1;
                    temp.temps.Add(a);
                    temp.SaveChanges();
                }
                return RedirectToAction("Medicine");
            }
#endregion
            #region Edit
            public ActionResult Edit(int? id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (id == null)
                {
                    return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
                }
                Medicine med = db.Medicines.Find(id);
                if (med == null)
                {
                    return HttpNotFound();
                }
                List<string> Names = new List<string>();
                foreach (Company a in db.Companies)
                {
                    Names.Add(a.Name);
                }
                List<string> chemical = new List<string>();
                foreach (ChemicalForm a in db.ChemicalForms)
                {
                    chemical.Add(a.ChemicalForm1);
                }
                ViewBag.chemicalForms = chemical;
                ViewBag.CompanyNames = Names;

                return View(med);
            }


            [HttpPost]
            public ActionResult Edit(Medicine Med)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (ModelState.IsValid)
                {
                    if (Med.Quantity == 0)
                    {
                        return HttpNotFound();

                    }
                    else
                    {
                        string Name = Request.Form["Names"].ToString();
                        string chem = Request.Form["Forms"].ToString();
                        Med.ChemicalForm = db.ChemicalForms.First(a => a.ChemicalForm1 == chem).Id;
                        Med.Company_ID = db.Companies.First(a => a.Name == Name).Id;
                        db.Entry(Med).State = EntityState.Modified;
                        db.SaveChanges();

                        if (CheckForInternetConnection() == true)
                        {
                            med med = new med();
                            med.Price = (float)Med.Price;
                            med.Name = Med.Name;
                            med.Quantity = Med.Quantity;
                            med.Pharmacy_id = 1;
                            central.Entry(med).State = EntityState.Modified;
                            central.SaveChanges();
                        }
                        else
                        {
                            temp a = new temp();
                            a.Edit = true;
                            a.Name = Med.Name;
                            a.Price = Med.Price;
                            a.Quantity = Med.Quantity;
                            a.Phar_id = 1;
                            temp.temps.Add(a);
                            temp.SaveChanges();
                        }
                        return RedirectToAction("Medicine");
                    }
                }
               
                return View(Med);

            }
#endregion

            public ActionResult Contact()
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                ViewBag.Message = "Your contact page.";

                return View();
            }
            #region Delete
            public ActionResult Delete(int id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (id == null)
                {
                    return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
                }
                Medicine Med=db.Medicines.Find(id);
                if (Med == null)
                {
                    return HttpNotFound();
                }
                return View(Med);

            }
            [HttpPost, ActionName("Delete")]
            public ActionResult DeleteConfirmed(int id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                Medicine Med = db.Medicines.Find(id);
                db.Medicines.Remove(Med);
                db.SaveChanges();
                if (CheckForInternetConnection() == true)
                {
                    med med = new med();
                    med.Price = (float)Med.Price;
                    med.Name = Med.Name;
                    med.Quantity = Med.Quantity;
                    med.Pharmacy_id = 1;
                    central.meds.Attach(med);
                    var y = (from x in central.meds where x.Name == med.Name && x.Pharmacy_id == 1 && x.Quantity == med.Quantity select x).First();
                    central.meds.Remove(y);
                    central.SaveChanges();
                   
                }
                else
                {
                    temp a = new temp();
                    a.Delete = true;
                    a.Name = Med.Name;
                    a.Price = Med.Price;
                    a.Quantity = Med.Quantity;
                    a.Phar_id = 1;
                    temp.temps.Add(a);
                    temp.SaveChanges();
                }
                return RedirectToAction("Medicine");
            }
            #endregion
            
            #region Sell
            public ActionResult Sell(int? id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (id == null)
                {
                    return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
                }
                Medicine Med = db.Medicines.Find(id);
                if (Med == null)
                {
                    return HttpNotFound();
                }
                return View(Med);
            }
            [HttpPost]
            public ActionResult Sell(int id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                Medicine Med = db.Medicines.Find(id);
                Sale add = new Sale();
                if (Med.Quantity == 0)
                    return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
                if (Med.Quantity == 1)
                {
                    db.Medicines.Remove(Med);
                    add.Medicine_Name = Med.Name;
                    add.Date_Sold = DateTime.Now;
                    add.Price = Med.Price;
                    db1.Sales.Add(add);
                    db.SaveChanges();
                    db1.SaveChanges();
                    if (CheckForInternetConnection() == true)
                    {
                        med med = new med();
                        med.Price = (float)Med.Price;
                        med.Name = Med.Name;
                        med.Quantity = Med.Quantity;
                        med.Pharmacy_id = 1;
                        central.meds.Remove(med);
                        central.SaveChanges();
                    }
                    else
                    {
                        temp a = new temp();
                        a.Delete = true;
                        a.Name = Med.Name;
                        a.Price = Med.Price;
                        a.Quantity = Med.Quantity;
                        a.Phar_id = 1;
                        temp.temps.Add(a);
                        temp.SaveChanges();
                    }
                    return RedirectToAction("Medicine");
                }
                else
                    Med.Quantity = Med.Quantity - 1;
                add.Medicine_Name = Med.Name;
                add.Date_Sold = DateTime.Now;
                add.Price = Med.Price;
                db1.Sales.Add(add);
                db.SaveChanges();
                db1.SaveChanges();
                return RedirectToAction("Medicine");
            }
            #endregion

            public ActionResult Search(string searchString,DateTime ? date)
            {
                
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (CheckForInternetConnection() == true)
                    Refresh1();
                //string searchString = id;
                var med = from m in db.Medicines
                             select m;

               
                if (!String.IsNullOrEmpty(searchString))
                {
                    med = med.Where(s => s.Name.Contains(searchString));
                }
                else if (date.HasValue)
                {
                    med = med.Where(s => s.Expire_Date < date);
                }
                
                return View(med);
            }

            public ActionResult Sales()
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                return View(db1.Sales.ToList());
            }

            #region CreateSell
            public ActionResult CreateSell()
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                return View();
            }
            [HttpPost]
            public ActionResult CreateSell(Sale item)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (ModelState.IsValid)
                {
                    db1.Sales.Add(item);
                    db1.SaveChanges();
                    return RedirectToAction("Sales");
                }
                return View("Sales");
            }
            #endregion

            public ActionResult DeleteSale(int? id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (id == null)
                {
                    return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
                }
                Sale item=db1.Sales.Find(id);
                if (item == null)
                {
                    return HttpNotFound();
                }
                return View(item);
            }
            [HttpPost]
            public ActionResult DeleteSale(int id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                Sale item = db1.Sales.Find(id);
                db1.Sales.Remove(item);
                db1.SaveChanges();
                return RedirectToAction("Sales");
            }
            #region EditSale
            public ActionResult EditSale(int ? id)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (id == null)
                {
                    return new HttpStatusCodeResult(HttpStatusCode.BadRequest);
                }
                Sale item = db1.Sales.Find(id);
                if (item == null)
                {
                    return HttpNotFound();
                }
                return View(item);
            }
            [HttpPost]
            public ActionResult EditSale(Sale item)
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                if (ModelState.IsValid)
                {

                    db1.Entry(item).State = EntityState.Modified;
                    db1.SaveChanges();
                    return RedirectToAction("Sales");
                }

                return View(item);
            }
            #endregion

            public ActionResult Notes()
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
            
                var med = from m in db.Medicines
                          select m;
                med = med.Where(s => s.Quantity<20);
                return View(med);

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
            public ActionResult Warehouse()
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");

                if (CheckForInternetConnection() == false)
                    return HttpNotFound();
                else
                {
                    return View(central.warehouses.ToList());
                }
            }
            
            public ActionResult ShowWarehouse(int ? id )
            {
                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");
                var war=from m in central.warehousemedicines 
                        select m;
                war=war.Where(s => s.Warehouse_id==id);
                return View(war);

            }
            public ActionResult SearchWarehouse(string searchString)
            {

                if (ConfirmLogIn() != true)
                    return RedirectToAction("Login", "Account");

                string fullUrl = Request.Url.ToString();
                string searchString1 = fullUrl.Split('/').Last();
                
                var med = from m in central.warehousemedicines
                             select m;
                var med1 = med;

                if (!String.IsNullOrEmpty(searchString))
                {
                    med = med.Where(s => s.Name.Contains(searchString));
                    return View(med);
                }
                med1 = med1.Where(s => s.Name.Contains(searchString1));
                return View(med1);
            }

            public ActionResult PharmacyDetails(int id)
            {
                if (ConfirmLogIn() == false)
                    return HttpNotFound();
                var a = central.pharmacies.Find(id);
                return View(a);

            }
            [HttpPost]
            public ActionResult PharmacyDetails(pharmacy a)
            {
                if (ConfirmLogIn() == false)
                    return HttpNotFound();
                central.Entry(a).State = EntityState.Modified;
                central.SaveChanges();
                return RedirectToAction("Index","Home");

            }
            public ActionResult Refresh1()
            {
                med add = new med();
                foreach (var a in temp.temps.ToList())
                {
                    if (a.create == true)
                    {
                        add.Name = a.Name;
                        add.Price = (float)a.Price;
                        add.Quantity = a.Quantity;
                        add.Pharmacy_id = a.Phar_id;
                        central.meds.Add(add);
                        central.SaveChanges();
                        temp.temps.Remove(a);
                        temp.SaveChanges();
                    }
                    if (a.Edit == true)
                    {
                        add.Name = a.Name;
                        add.Price = (float)a.Price;
                        add.Quantity = a.Quantity;
                        add.Pharmacy_id = a.Phar_id;
                        central.Entry(add).State = EntityState.Modified;
                        central.SaveChanges();
                        temp.temps.Remove(a);
                        temp.SaveChanges();
                    }
                    if (a.Delete == true)
                    {
                        add.Name = a.Name;
                        add.Price = (float)a.Price;
                        add.Quantity = a.Quantity;
                        add.Pharmacy_id = a.Phar_id;
                        central.meds.Attach(add);
                        var y = (from x in central.meds where x.Name == add.Name && x.Pharmacy_id == 1 && x.Quantity == add.Quantity select x).First();
                        central.meds.Remove(y);
                        central.SaveChanges();
                        temp.temps.Remove(a);
                        temp.SaveChanges();
                    }
                    if (a.Demand == true)
                    {

                        var find = central.demands.Where(s => s.Name == a.Name && s.Price == a.Price && s.Quantity == a.Quantity && s.Phar_id == 1 && s.Status == true);
                        if (find == null)
                            return View();
                        else
                        {
                            add.Name = a.Name;
                            add.Price = (float)a.Price;
                            add.Quantity = a.Quantity;
                            add.Pharmacy_id = a.Phar_id;

                            central.meds.Add(add);
                            central.SaveChanges();

                            Medicine med = new Medicine();
                            med.Name = a.Name;
                            med.Price = a.Price;
                            med.Quantity = a.Quantity;
                            med.Expire_Date = find.Max(s => s.ExpiredDate);
                            med.ChemicalForm = 2;
                            med.Company_ID = 8;
                            db.Medicines.Add(med);
                            db.SaveChanges();
                            temp.temps.Remove(a);
                            temp.SaveChanges();

                        }
                    }

                   // Response.Write("Done");
                }

                return View();
            }

            public void AddallMedicine()
            {
                med temp = new med();
                foreach (var a in db.Medicines.ToList())
                {
                    temp.Name = a.Name;
                    temp.Pharmacy_id=1;
                    temp.Price= (float)a.Price;
                    temp.Quantity = a.Quantity;
                    central.meds.Add(temp);
                    central.SaveChanges();
                }
            }
            public ActionResult Refresh(string submit)
            {
                var add = new med();
                foreach (var a in temp.temps.ToList())
                {
                    if (a.create == true)
                    {
                        add.Name = a.Name;
                        add.Price = (float)a.Price;
                        add.Quantity = a.Quantity;
                        add.Pharmacy_id = a.Phar_id;
                        central.meds.Add(add);
                        central.SaveChanges();
                        temp.temps.Remove(a);
                        temp.SaveChanges();
                    }
                    if (a.Edit == true)
                    {
                        add.Name = a.Name;
                        add.Price = (float)a.Price;
                        add.Quantity = a.Quantity;
                        add.Pharmacy_id = a.Phar_id;
                        central.Entry(add).State = EntityState.Modified;
                        central.SaveChanges();
                        temp.temps.Remove(a);
                        temp.SaveChanges();
                    }
                    if (a.Delete == true)
                    {
                        add.Name = a.Name;
                        add.Price = (float)a.Price;
                        add.Quantity = a.Quantity;
                        add.Pharmacy_id = a.Phar_id;
                        central.meds.Attach(add);
                        var y = (from x in central.meds where x.Name == add.Name && x.Pharmacy_id == 1 && x.Quantity == add.Quantity select x).First();
                        central.meds.Remove(y);
                        central.SaveChanges();
                        temp.temps.Remove(a);
                        temp.SaveChanges();
                    }
                     if (a.Demand == true)
                    {
   
                        var find = central.demands.Where(s => s.Name == a.Name && s.Price == a.Price && s.Quantity == a.Quantity && s.Phar_id == a.Phar_id && s.Status == true);
                        if(find==null)
                            return View();
                        else{
                        add.Name = a.Name;
                        add.Price =(float) a.Price;
                        add.Quantity = a.Quantity;
                        add.Pharmacy_id = a.Phar_id;
                         
                        central.meds.Add(add);
                        central.SaveChanges();

                        Medicine med = new Medicine();
                        med.Name = a.Name;
                        med.Price = a.Price;
                        med.Quantity = a.Quantity;
                        med.Expire_Date = find.Max(s => s.ExpiredDate);
                        med.ChemicalForm = 2;
                        med.Company_ID = 8;
                        db.Medicines.Add(med);
                        db.SaveChanges();
                        temp.temps.Remove(a);
                        temp.SaveChanges();

                    }
                        }
                
                Response.Write(submit);
                }

                return View();
            }


        }
        
    }
