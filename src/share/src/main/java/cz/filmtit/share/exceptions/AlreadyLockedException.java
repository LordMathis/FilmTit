/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.filmtit.share.exceptions;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

/**
 * The exception is thrown if the Translation Result that the user wants to lock
 * is already locked by another user
 *
 * @author Matúš Námešný
 */
public class AlreadyLockedException extends Exception implements Serializable, IsSerializable {

    AlreadyLockedException() {
    }

    public AlreadyLockedException(String message) {
        super(message);
    }
}
