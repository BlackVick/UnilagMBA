package com.tti.unilagmba;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ExpandableListView;

import com.tti.unilagmba.ViewHolder.ExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Faq extends AppCompatActivity {

    private Toolbar mToolbar;
    private ExpandableListView listView;
    private ExpandableListAdapter adapter;
    private List<String> listFaqTitle;
    private HashMap<String, List<String>> listHash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        mToolbar = (Toolbar)findViewById(R.id.faqAppBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("FAQ");

        listView = (ExpandableListView)findViewById(R.id.faqExpandable);
        initData();
        adapter = new ExpandableListAdapter(this, listFaqTitle, listHash);
        listView.setAdapter(adapter);
    }

    private void initData() {
        listFaqTitle = new ArrayList<>();
        listHash = new HashMap<>();

        listFaqTitle.add("HOW DO I SIGN UP?");
        listFaqTitle.add("WHAT SHOULD I USE TO LOG IN?");
        listFaqTitle.add("WHAT DO I DO WHEN I FORGET MY PASSWORD?");
        listFaqTitle.add("WHAT DO I DO WHEN I FORGET MY SECURE CODE?");
        listFaqTitle.add("NAVIGATION DRAWER OPTIONS");
        listFaqTitle.add("HOW DO I UPDATE MY PROFILE?");
        listFaqTitle.add("HOW DO I CHANGE MY PASSWORD?");
        listFaqTitle.add("WHAT IS THE STUDENT PORTAL?");
        listFaqTitle.add("HOW DO I USE THE UPDATE NEWS FEED FEATURE?");
        listFaqTitle.add("HOW DO I USE THE ELECTION VOTING PORTAL?");
        listFaqTitle.add("WHAT IS THE UNILAG MBA EXCOS?");
        listFaqTitle.add("WHAT CAN I DO IN SETTINGS?");
        listFaqTitle.add("LOGOUT");
        listFaqTitle.add("DASHBOARD");
        listFaqTitle.add("NEWS FEED");
        listFaqTitle.add("MATERIALS");
        listFaqTitle.add("PAST QUESTIONS");
        listFaqTitle.add("ANNOUNCEMENT");
        listFaqTitle.add("MATERIAL UPLOAD");
        listFaqTitle.add("ORGANIZER");
        listFaqTitle.add("CHAT");
        listFaqTitle.add("ADD FRIEND");
        listFaqTitle.add("FRIEND REQUEST");
        listFaqTitle.add("ACTIVE CHATS");


        List<String> signUp = new ArrayList<>();
        signUp.add("The user signs up by using the Unilag MBA Matric number, Full Name, Password, and a secure code of your choice which must be a set of four digit.");
        signUp.add("Click on the register button and fill in the above mentioned details");

        List<String> login = new ArrayList<>();
        login.add("The user Logs in with the Unilag Matric number and the password which was used to register.");

        List<String> forgotPass = new ArrayList<>();
        forgotPass.add("A registered user can retrieve his/her password by providing the Unilag Matric number and the secure code chosen at the point of registration.");

        List<String> forgotSecureCode = new ArrayList<>();
        forgotSecureCode.add("A user can retrieve his/her secure code by providing his/her Full name, Unilag Matric number and username which was used to register the account.");
        forgotSecureCode.add("Once it is confirmed that the user is valid, their secure code and password would be sent to their email.");

        List<String> navOptions = new ArrayList<>();
        navOptions.add("The Navigation drawer houses the users profile picture, Full name and Matric Number.");

        List<String> updateProf = new ArrayList<>();
        updateProf.add("You can update your profile in the update your info section. You can change your Profile picture, Status and Username here.");
        updateProf.add("Changing profile picture is done by clicking on the camera picture at the low right side of the current profile image.");

        List<String> changePass = new ArrayList<>();
        changePass.add("You can change your password by selecting Change Your Password section and inputing your former password then typing in your new password and confirming it.");

        List<String> studentPortal = new ArrayList<>();
        studentPortal.add("The student portal is a tab that takes the user to the Unilag official Student portal log in page.");

        List<String> newsUpdate = new ArrayList<>();
        newsUpdate.add("The Update News Feed feature consist of three sub-features. This is the Main News Feed, Missing Items and Events.");
        newsUpdate.add("Each feature can be used according to it's nomenclature.");
        newsUpdate.add("Main News Feed: This is used to communicate News on the App. You need to enter the topic of the news, the content of news, the type of news");
        newsUpdate.add("Missing Items is used to put to notice of the public missing items or lost and found items. ");
        newsUpdate.add("Events: This is used to communicate News pertaining to event(s) which will take place. You need to enter the title of the event, the content of the event.");
        newsUpdate.add("The picture upload button is located below the broadcast detail textbox to the lower right");
        newsUpdate.add("A picture relational to the news about to be posted is required. Once the above have been provided, the user clicks the 'Send News' button to publish the news.");
        newsUpdate.add("NOTE: All type of news require a picture upload before the news can be published.");

        List<String> electionPortal = new ArrayList<>();
        electionPortal.add("The election voting portal is only accessible when the Unilag MBA Students are getting ready to exercise their civic right within the confines of the University.");
        electionPortal.add("The Different posts held thus have the list of candidates which can be voted for.");
        electionPortal.add("Once a user cast his/her vote, they will be asked to confirm their choice because once the vote is casted, it can't be reversed.");
        electionPortal.add("On confirming whoever they choose to cast their vote for, the post and the aspirants would disappear.");

        List<String> excos = new ArrayList<>();
        excos.add("This is a list of all the executive members of the present regime. On clicking any executive member's profile, a profile picture, Full name, email address, position held, contact number and office location will pop up.");

        List<String> quickSetting = new ArrayList<>();
        quickSetting.add("The setting has a feature which you can choose to enable or disable. This feature is the Subcription to News Feed feature. Once you enable the subscribe to News Feed feature, you'll be able to get all the notification on News Feed posted on the application. However, if you don't enable this feature, you won't get any notification pertaining to News Feed.");

        List<String> logout = new ArrayList<>();
        logout.add("This feature is the last of the Navigation Drawer options. The logout feature logs a user out of the application once clicked. This thus refrains the user from all the features which the application provides. The user will however need to re-login with his/her Unilag MBA Matric number and password in order gain access to the functionalities of the application.");

        List<String> dashboard = new ArrayList<>();
        dashboard.add("The Dashboard is the page that holds the News Feed page, Main Courses Page, Specializations Page, Organizer Page, Chat Page and the Top Right Option Button which houses the Main Feed News Page, Missing Items News Page and the Events News Page.");

        List<String> newsFeed = new ArrayList<>();
        newsFeed.add("This is the page where the Main News Feed posted shows.The News posted is arranged chronoligically with the daye and time of post.");

        List<String> materials = new ArrayList<>();
        materials.add("This tab contains all available course in Unilag MBA and all can be accessed by clicking on them");
        materials.add("Clicking the course opens a page which houses the different materials that are uploaded.");
        materials.add("Settings for notification can be found at the top right corner is found at the top right corner of the Course Title Page. It allows the user to enable or disable the subscription feature which allows the user get notified when a material for that particular course is uploaded.");

        List<String> pastQuestions = new ArrayList<>();
        pastQuestions.add("This page holds the different past questions uploaded for that particular course. Past questions can be uploaded by clicking the document upload icon at the bottom right corner of the page. Past question name, Course name, material type is required to complete the upload.");

        List<String> announcement = new ArrayList<>();
        announcement.add("The announcer's name and the content of announcement is required in order to put an announcement in the Course Page");

        List<String> materialUpload = new ArrayList<>();
        materialUpload.add("The Material Name, Course Name, Material brief information and material type is required to complete the upload. Once all information has been provided accordingly, select the file to be uploaded and select yes.");

        List<String> organizer = new ArrayList<>();
        organizer.add("This page can be accessed by swiping from right to left from the Specializations Page. It consists of Monday to Saturday which you can use to organize your activities. A Add New Task icon can be found at the bottom right corner of each Day of the week's page.");

        List<String> chat = new ArrayList<>();
        chat.add("The chat platform is where you can communicate with your friends once either added a friend(s) or you have been added.");

        List<String> addFriend = new ArrayList<>();
        addFriend.add("The add friend icon can be found on the top right corner of the Chat Page. ");
        addFriend.add("You click this icon so that you can add a friend searching for the username of your friend.");

        List<String> friendRequest = new ArrayList<>();
        friendRequest.add("This is the page that has the list of friend requests sent to a user by other users.");

        List<String> activeChat = new ArrayList<>();
        activeChat.add("The list of active chats is on this page. This is where a user finds all the friends he/she has added and can scroll and click any of the active chat to initiate a conversation with his/her friend. Images can also be exchanged in the chats.");



        listHash.put(listFaqTitle.get(0), signUp);
        listHash.put(listFaqTitle.get(1), login);
        listHash.put(listFaqTitle.get(2), forgotPass);
        listHash.put(listFaqTitle.get(3), forgotSecureCode);
        listHash.put(listFaqTitle.get(4), navOptions);
        listHash.put(listFaqTitle.get(5), updateProf);
        listHash.put(listFaqTitle.get(6), changePass);
        listHash.put(listFaqTitle.get(7), studentPortal);
        listHash.put(listFaqTitle.get(8), newsUpdate);
        listHash.put(listFaqTitle.get(9), electionPortal);
        listHash.put(listFaqTitle.get(10), excos);
        listHash.put(listFaqTitle.get(11), quickSetting);
        listHash.put(listFaqTitle.get(12), logout);
        listHash.put(listFaqTitle.get(13), dashboard);
        listHash.put(listFaqTitle.get(14), newsFeed);
        listHash.put(listFaqTitle.get(15), materials);
        listHash.put(listFaqTitle.get(16), pastQuestions);
        listHash.put(listFaqTitle.get(17), announcement);
        listHash.put(listFaqTitle.get(18), materialUpload);
        listHash.put(listFaqTitle.get(19), organizer);
        listHash.put(listFaqTitle.get(20), chat);
        listHash.put(listFaqTitle.get(21), addFriend);
        listHash.put(listFaqTitle.get(22), friendRequest);
        listHash.put(listFaqTitle.get(23), activeChat);
    }
}
