//------------------------------------------------------------------------------
// <auto-generated>
//    This code was generated from a template.
//
//    Manual changes to this file may cause unexpected behavior in your application.
//    Manual changes to this file will be overwritten if the code is regenerated.
// </auto-generated>
//------------------------------------------------------------------------------

namespace Demo.Models
{
    using System;
    using System.Collections.Generic;
    
    public partial class pharmacy
    {
        public pharmacy()
        {
            this.demands = new HashSet<demand>();
            this.meds = new HashSet<med>();
            this.xmlphars = new HashSet<xmlphar>();
            this.xmlphars1 = new HashSet<xmlphar>();
        }
    
        public int id { get; set; }
        public string Name { get; set; }
        public double latitude { get; set; }
        public double langtitude { get; set; }
        public string address { get; set; }
        public int Tel { get; set; }
        public string Open { get; set; }
    
        public virtual ICollection<demand> demands { get; set; }
        public virtual ICollection<med> meds { get; set; }
        public virtual ICollection<xmlphar> xmlphars { get; set; }
        public virtual ICollection<xmlphar> xmlphars1 { get; set; }
    }
}
