This project is an Android application designed to facilitate time tracking and 
task management. It provides features for managing categories, setting time periods, 
logging timesheet entries, and more.

Getting Started
To get started with this project, follow these steps:

Clone this repository to your local machine.
Open the project in Android Studio.
Build and run the project on an Android emulator or physical device.

Dependencies
AndroidX
CardView
RecyclerView

Features
Login Page: Provides a login interface for users to access the application securely.
Register: Proivides a register interface for users to create an account.
Categories Management: Allows users to add and manage categories.
Timesheet Entry: Allows users to log timesheet entries with details such as name, date, category, description, and optional picture attachment.
Timer Functionality: Includes a timer feature for tracking the duration of tasks.
Picture Attachment: Allows users to attach pictures to timesheet entries for reference.
ProgressBar: Each time sheet entry has a progress bar indicate the progress of the task.
Estimated Time: An estimated time is added and displayed for each time sheet entry the user adds.
Daily Goals: Allows users to add daily goals.
Daily Goal Tracking:Users can track their goals and set status for the progress.

1. Login Screen
Users can enter their email and password to login
   - To use this screen:
     - Fill in the email and password fields.
     - Click the Login button 
     - The user wil then be taken to the dashboard

2. Register
- This screen allows users to register for your application.
   - Users can enter their email and password.
   - To use this screen:
     - Fill in the email and password fields.
     - Click the "Register" button to register.
     - There's a "Login" button, you can switch to the login page by clicking it.

3. DashBoard
- On this screen the user can navigate to other screens such as TimeSheet Entries, Set Daily Goals and Categories.
- To use this page click on the respected block to go to that screen

4. TimeSheet Entries
- Here the user see 3 buttons
 - New Entry
 - Add Category
 - Daily Goals
- Clicking a button will take you to the respective screen.
- Below the buttons is the list of time sheet entries the user has made.

5. New Time Sheet Entry
- Here the user can add a time sheet entry.
- The user provides a name, date, chooses a category, selects a date, a time and can optionally add a picture.
- Once completed they can click done to add the entry

6. Category
-Here the user can add a category.
-Enter a name for the category and then click done to add it.

7. Set Daily Goals
-Here the user can set daily goal.
-The user enters a goal and then it is added with todays date and the name.
-Every goal add is created with the default status of in-progress.
-Clicking the button below a goal allows you to change the status to done or in-progress.
-The app keeps a running total of the goals and checks how many have been set to done.

Authors
St10083156
St10165886
St10083498
St10034485

Technologies and Languages Used

Backend:
Kotlin programming language

Development Environment:
Android Studio

Database:
Firebase
FireStore

Version Control:
Git


