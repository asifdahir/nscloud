/**
 *   ownCloud Android client application
 *
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

package com.nscloud.android.ui.dialog;

import com.nscloud.android.R;
import com.nscloud.android.datamodel.OCFile;
import com.nscloud.lib.resources.files.FileUtils;
import com.nscloud.android.ui.activity.ComponentsGetter;

import android.support.v7.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 *  Dialog to input the name for a new folder to create.  
 * 
 *  Triggers the folder creation when name is confirmed.
 */
public class CreateFolderDialogFragment
        extends DialogFragment implements DialogInterface.OnClickListener {

    private static final String ARG_PARENT_FOLDER = "PARENT_FOLDER";
    
    public static final String CREATE_FOLDER_FRAGMENT = "CREATE_FOLDER_FRAGMENT";

    /**
     * Public factory method to create new CreateFolderDialogFragment instances.
     *
     * @param parentFolder            Folder to create
     * @return                        Dialog ready to show.
     */
    public static CreateFolderDialogFragment newInstance(OCFile parentFolder) {
        CreateFolderDialogFragment frag = new CreateFolderDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARENT_FOLDER, parentFolder);
        frag.setArguments(args);
        return frag;
        
    }

    private OCFile mParentFolder;
    
    
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mParentFolder = getArguments().getParcelable(ARG_PARENT_FOLDER);
        
        // Inflate the layout for the dialog
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.edit_box_dialog, null);
        
        // Setup layout 
        EditText inputText = ((EditText)v.findViewById(R.id.user_input));
        inputText.setText("");
        inputText.requestFocus();
        
        // Build the dialog  
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(v)
               .setPositiveButton(R.string.common_ok, this)
               .setNegativeButton(R.string.common_cancel, this)
               .setTitle(R.string.uploader_info_dirname);
        Dialog d = builder.create();
        d.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        return d;
    }    
    
    
    @Override
    public void onClick(DialogInterface dialog, int which) {
        if (which == AlertDialog.BUTTON_POSITIVE) {
            String newFolderName = 
                    ((TextView)(getDialog().findViewById(R.id.user_input)))
                        .getText().toString().trim();
            
            if (newFolderName.length() <= 0) {
                Toast.makeText(
                        getActivity(),
                        R.string.filename_empty, 
                        Toast.LENGTH_LONG).show();
                return;
            }
            boolean serverWithForbiddenChars = ((ComponentsGetter)getActivity()).
                    getFileOperationsHelper().isVersionWithForbiddenCharacters();

            if (!FileUtils.isValidName(newFolderName, serverWithForbiddenChars)) {
                int messageId = 0;
                if (serverWithForbiddenChars) {
                    messageId = R.string.filename_forbidden_charaters_from_server;
                } else {
                    messageId = R.string.filename_forbidden_characters;
                }
                Toast.makeText(getActivity(), messageId, Toast.LENGTH_LONG).show();

                return;
            }
            
            String path = mParentFolder.getRemotePath();
            path += newFolderName + OCFile.PATH_SEPARATOR;
            ((ComponentsGetter)getActivity()).
                getFileOperationsHelper().createFolder(path, false);
        }
    }
        
}
