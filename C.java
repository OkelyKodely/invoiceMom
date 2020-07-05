/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mom;

/**
 *
 * @author dhcho
 */
public abstract class C extends Object {

    public String compareTo(String a, String b) {
        if(a.charAt(0) > b.charAt(0)) {
            return a;
        } else {
            return b;
        }
    }
}
