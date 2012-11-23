/*
 *  Copyright 2012 Kobi Krasnoff
 * 
 * This file is part of Call recorder For Android.

    Call recorder For Android is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Call recorder For Android is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Call recorder For Android.  If not, see <http://www.gnu.org/licenses/>
 */       
package com.call.recorder;

public class Model implements Comparable<Model> {
    private String callName;
    private String userNameFromContact;
   
    public String getUserNameFromContact() {
        return userNameFromContact;
	}
	
	public void setUserNameFromContact(String userNameFromContact) {
	        this.userNameFromContact = userNameFromContact;
	}
    
    public String getCallName() {
           return callName;
    }

    public void setCallName(String callName) {
           this.callName = callName;
    }

    public Model(String callName)
    {
           this.callName = callName;
    }

    public int compareTo(Model another) {
           Long date1 = Long.valueOf(this.getCallName().substring(1, 15));
           Long date2 = Long.valueOf(another.getCallName().substring(1, 15));
           return (date2>date1 ? -1 : (date2==date1 ? 0 : 1));
    }
   
   
}
