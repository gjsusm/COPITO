# Cloud Functions for Ice Cream POS

This directory contains the server-side logic for the Ice Cream POS application, implemented as Firebase Cloud Functions.

## Functions

- **`createNewUser`**: A callable function that allows an authenticated administrator to securely create a new user. It creates a user in Firebase Authentication and a corresponding user profile document in Firestore.

## Deployment

To deploy these functions to your Firebase project, you need to have the Firebase CLI (Command Line Interface) installed on your local machine.

### Prerequisites

1.  **Node.js**: Cloud Functions run in a Node.js environment. You need to have Node.js installed.
2.  **Firebase CLI**: If you don't have it, install it globally by running:
    ```bash
    npm install -g firebase-tools
    ```

### Deployment Steps

1.  **Login to Firebase:**
    ```bash
    firebase login
    ```

2.  **Initialize Firebase in your project (if you haven't already):**
    From the root directory of the entire project (not inside the `functions` directory), run:
    ```bash
    firebase init
    ```
    Follow the prompts. When it asks which features to set up, make sure to select **Functions**. It will ask you to select the project and will detect the existing `functions` directory.

3.  **Install Dependencies:**
    Navigate into the `functions` directory and install the required packages:
    ```bash
    cd functions
    npm install
    ```

4.  **Deploy:**
    From within the `functions` directory, run the deploy command:
    ```bash
    firebase deploy --only functions
    ```

After the deployment is successful, the `createNewUser` function will be live and callable from the Android application.
