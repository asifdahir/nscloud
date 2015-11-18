/**
 *   ownCloud Android client application
 *
 *   @author masensio
 *   @author David A. Velasco
 *   Copyright (C) 2015 ownCloud Inc.
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License version 2,
 *   as published by the Free Software Foundation.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.nscloud.android.operations;

import java.util.ArrayList;

import com.nscloud.lib.common.NsCloudClient;
import com.nscloud.lib.common.operations.RemoteOperationResult;
import com.nscloud.lib.common.utils.Log_OC;
import com.nscloud.lib.resources.shares.OCShare;
import com.nscloud.lib.resources.shares.GetRemoteSharesOperation;
import com.nscloud.android.operations.common.SyncOperation;

/**
 * Access to remote operation to get the share files/folders
 * Save the data in Database
 */

public class GetSharesOperation extends SyncOperation {

    private static final String TAG = GetSharesOperation.class.getSimpleName();

    @Override
    protected RemoteOperationResult run(NsCloudClient client) {
        GetRemoteSharesOperation operation = new GetRemoteSharesOperation();
        RemoteOperationResult result = operation.execute(client);

        if (result.isSuccess()) {

            // Update DB with the response
            Log_OC.d(TAG, "Share list size = " + result.getData().size());
            ArrayList<OCShare> shares = new ArrayList<OCShare>();
            for(Object obj: result.getData()) {
                shares.add((OCShare) obj);
            }

            getStorageManager().saveSharesDB(shares);
        }

        return result;
    }

}
