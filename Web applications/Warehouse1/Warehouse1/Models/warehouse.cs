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
    
    public partial class warehouse
    {
        public warehouse()
        {
            this.demands = new HashSet<demand>();
            this.warehousemedicines = new HashSet<warehousemedicine>();
        }
    
        public int id { get; set; }
        public string Name { get; set; }
        public string address { get; set; }
        public int tel { get; set; }
    
        public virtual ICollection<demand> demands { get; set; }
        public virtual ICollection<warehousemedicine> warehousemedicines { get; set; }
    }
}
