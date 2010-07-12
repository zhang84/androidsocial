package main.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ActivityFileBrowser extends ListActivity {

	private enum DISPLAYMODE {
		ABSOLUTE, RELATIVE;
	}

	private final DISPLAYMODE displayMode = DISPLAYMODE.ABSOLUTE;
	private List<String> directoryEntries = new ArrayList<String>();
	private File currentDirectory = new File("/");

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		// setContentView() gets called within the next line,
		// so we do not need it here.
		browseToRoot();
	}

	/**
	 * This function browses to the root-directory of the file-system.
	 */
	private void browseToRoot() {
		browseTo(new File("/"));
	}

	/**
	 * This function browses up one level according to the field:
	 * currentDirectory
	 */
	private void upOneLevel() {
		if (this.currentDirectory.getParent() != null)
			this.browseTo(this.currentDirectory.getParentFile());
	}

	private void browseTo(final File aDirectory) {
		if (aDirectory.isDirectory()) {
			this.currentDirectory = aDirectory;
			fill(aDirectory.listFiles());
		} else {
			OnClickListener okButtonListener = new OnClickListener() {
				// @Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent myIntent = new Intent(
							android.content.Intent.ACTION_VIEW,
							Uri.fromFile(new File("file://"
									+ aDirectory.getAbsolutePath())));
					startActivity(myIntent);
				}
			};
			DialogInterface.OnCancelListener cancelButtonListener = new DialogInterface.OnCancelListener( ) {
				
				public void onCancel(DialogInterface dialog) {
				}
			}; 
		    new AlertDialog.Builder(this)
		      .setMessage("Phyllis is calling")
		      .setTitle( "Question")
		      .setPositiveButton( "OK", okButtonListener)
		      .setOnCancelListener( cancelButtonListener)
		      .show();
		}
	}

	private void fill(File[] files) {
		this.directoryEntries.clear();

		// Add the "." and the ".." == 'Up one level'
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		this.directoryEntries.add(".");

		if (this.currentDirectory.getParent() != null)
			this.directoryEntries.add("..");

		switch (this.displayMode) {
		case ABSOLUTE:
			for (File file : files) {
				this.directoryEntries.add(file.getPath());
			}
			break;
		case RELATIVE: // On relative Mode, we have to add the current-path to
						// the beginning
			int currentPathStringLenght = this.currentDirectory
					.getAbsolutePath().length();
			for (File file : files) {
				this.directoryEntries.add(file.getAbsolutePath().substring(
						currentPathStringLenght));
			}
			break;
		}

		ArrayAdapter<String> directoryList = new ArrayAdapter<String>(this,
				R.layout.filebrowser, this.directoryEntries);

		this.setListAdapter(directoryList);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		int selectionRowID = (int) this.getSelectedItemId();
		String selectedFileString = this.directoryEntries.get(selectionRowID);
		if (selectedFileString.equals(".")) {
			// Refresh
			this.browseTo(this.currentDirectory);
		} else if (selectedFileString.equals("..")) {
			this.upOneLevel();
		} else {
			File clickedFile = null;
			switch (this.displayMode) {
			case RELATIVE:
				clickedFile = new File(this.currentDirectory.getAbsolutePath()
						+ this.directoryEntries.get(selectionRowID));
				break;
			case ABSOLUTE:
				clickedFile = new File(
						this.directoryEntries.get(selectionRowID));
				break;
			}
			if (clickedFile != null)
				this.browseTo(clickedFile);
		}
	}
}
