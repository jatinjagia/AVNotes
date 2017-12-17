# AVNotes

LOGIN ACTIVITY:

This contains the button to SignIn with google account.

MAIN ACTIVITY:

This is the main activity where you can create notes and open already written notes via the app.
There are four buttons in this activity.
1. Open button - To open already saved files on google drive via the app.Although it will display files not created via app but they cannot be opened.

2. Create button - To use the app you need to first create a  file in the drive. Untill you do so the save button will be disabled. This will open a dialog to choose directory to save the file and choose a file name.

3. Save button- After you have created a file this button will be enabled and after making your changes you can save the file on drive via this button.

4. Signout button- This will signout your google account and take you back to login screen.

BASEDRIVEACTIVITY:

This is the abstract class handling all the funtionality to connect to google drive api.
