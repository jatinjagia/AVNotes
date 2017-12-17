package com.example.jatin.avnotes.tasks;

/**
 * Created by jatin on 16/12/17.
 */
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource.MetadataResult;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataChangeSet;
import com.google.android.gms.tasks.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import java.util.concurrent.ExecutionException;

/**
 * An async task to open, make changes to and close a file.
 */
public abstract class EditDriveFileAsyncTask
        extends AsyncTask<DriveId, Boolean, Boolean> {

    /**
     * Represents the delta of the metadata changes and keeps a pointer to the file
     * contents to be stored permanently.
     */
    public class Changes {
        private MetadataChangeSet mMetadataChangeSet;
        private DriveContents mDriveContents;

        public Changes(MetadataChangeSet metadataChangeSet, DriveContents contents) {
            mMetadataChangeSet = metadataChangeSet;
            mDriveContents = contents;
        }

        public MetadataChangeSet getMetadataChangeSet() {
            return mMetadataChangeSet;
        }

        public DriveContents getDriveContents() {
            return mDriveContents;
        }
    }

    private static final String TAG = "EditDriveFileAsyncTask";

    private DriveResourceClient driveResourceClient;

    /**
     * Constructor.
     *
     * @param driveResourceClient A connected {@link DriveResourceClient} instance.
     */
    public EditDriveFileAsyncTask(DriveResourceClient driveResourceClient) {
        this.driveResourceClient = driveResourceClient;
    }

    /**
     * Handles the editing to file metadata and contents.
     */
    public abstract Changes edit(DriveContents driveContents);

    /**
     * Opens contents for the given file, executes the editing tasks, saves the
     * metadata and content changes.
     */
    @Override
    protected Boolean doInBackground(DriveId... params) {
        DriveFile file = params[0].asDriveFile();
        try {
            DriveContents contents =
                    Tasks.await(driveResourceClient.openFile(file, DriveFile.MODE_WRITE_ONLY));

            Changes changes = edit(contents);
            MetadataChangeSet changeSet = changes.getMetadataChangeSet();
            DriveContents updatedContents = changes.getDriveContents();

            if (changeSet != null) {
                Tasks.await(driveResourceClient.updateMetadata(file, changeSet));
            }

            if (updatedContents != null) {
                Tasks.await(driveResourceClient.commitContents(updatedContents, changeSet));
            }
        } catch (ExecutionException | InterruptedException e) {
            Log.e(TAG, "Error editing DriveFile.", e);
            return false;
        }

        return true;
    }
}
