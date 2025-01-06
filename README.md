# Fast Messages Android App 💬

📍 Features:
- **Send SMS**: Allows the user to send a text message to a phone number.
- **Send MMS**: Allows the user to send an MMS (multimedia message) with a picture and text message.
- **Capture Photo**: Lets the user capture a photo using the device's camera.
- **Save Image**: Allows the user to save a captured image to the device's gallery.
- **Preview MMS**: Displays the captured image and message before sending via MMS.

📍 How to Set Up and Run the Application:
1. Clone or download the repository to your local machine.
2. Open the project in **Android Studio**.
3. Make sure to update the SDK and dependencies by syncing the project with Gradle files.
4. Connect an Android device or use an emulator.
5. Build and run the app by clicking on the **Run** button (green triangle) in Android Studio.

📍 Required Permissions and Their Purpose:
- `android.permission.READ_PHONE_STATE`: Required to read the phone's state (e.g., checking if a phone call is active).
- `android.permission.SEND_SMS`: Required to send SMS messages.
- `android.permission.CAMERA`: Required to access the device's camera to capture photos.
- `android.permission.WRITE_EXTERNAL_STORAGE`: Required to save images to the device's storage.
- `android.hardware.telephony`: Allows access to the device's telephony features, used for sending SMS.
- `android.hardware.camera`: Ensures the app can access the device's camera hardware for taking pictures.

📍 How to Use the App:
1. *Capture Photo*: Tap the camera icon to capture a photo using the device's camera.
2. *Enter Phone Number and Message*: Enter the phone number and the text message in the provided fields.
3. *Send SMS*: Tap the "Send SMS" button to send the text message to the entered phone number.
4. *Send MMS*: Enter a phone number and message, select an image, and preview the multimedia (image + message) before sending via MMS or other available apps (e.g., WhatsApp).
5. *Save Image*: Tap the "Save" button to save the captured image to the device's gallery for future use.

📝 Notes:
- The app requires a valid phone number to send SMS/MMS messages.
- Ensure that the required permissions are granted during runtime.
