//------------------------------------------------------------------------------
// <auto-generated>
//    This code was generated from a template.
//
//    Manual changes to this file may cause unexpected behavior in your application.
//    Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Warehouse1.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class demand
    {
        public int id { get; set; }
        public int Phar_id { get; set; }
        public int warhouse_id { get; set; }
        public string Name { get; set; }
        public int Quantity { get; set; }
        public float Price { get; set; }
        public System.DateTime ExpiredDate { get; set; }
        public string companyName { get; set; }
        public string chemicalForm { get; set; }
        public bool Status { get; set; }
        public System.DateTime Date_submitted { get; set; }
    
        public virtual pharmacy pharmacy { get; set; }
        public virtual warehouse warehouse { get; set; }
    }
}
