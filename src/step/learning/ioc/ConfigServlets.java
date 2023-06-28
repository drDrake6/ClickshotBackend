package step.learning.ioc;

import com.google.inject.servlet.ServletModule;
//import step.learning.*;
//import step.learning.filters.AuthFilter;
import step.learning.filters.DataFilter;
import step.learning.filters.EncodeFilter;
import step.learning.filters.LogRequestFilter;
import step.learning.servlets.*;


public class ConfigServlets extends ServletModule {
    @Override
    protected void configureServlets() {
        filter("/*").through(EncodeFilter.class);
        filter("/*").through(LogRequestFilter.class);
        filter("/*").through(DataFilter.class);

        serve("/allUsers").with(AllUsersServlet.class);
        serve("/add").with(AddUserServlet.class);
        serve("/delete").with(DeleteUserServlet.class);
        serve("/authorize").with(AuthorizeServlet.class);
        serve("/confirm").with(ConfirmEmailServlet.class);
        serve("/restore").with(RestorePasswordServlet.class);
        serve("/media/*").with(MediaServlet.class);
        serve("/ava").with(AvaImageServlet.class);
        serve("/posts").with(PostsServlet.class);
        serve("/addPost").with(AddPostServlet.class);
        serve("/deletePost").with(DeletePostServlet.class);
        serve("/taggedPeople").with(TaggedPeopleServlet.class);
        serve("/like").with(LikeServlet.class);
        serve("/save").with(SaveServlet.class);
        serve("/hasPost").with(HasPostServlet.class);
        serve("/ban").with(BanPostServlet.class);
        serve("/comments").with(CommentsServlet.class);
        serve("/addComment").with(AddCommentServlet.class);
        serve("/deleteComment").with(DeleteCommentServlet.class);
        serve("/addAnswer").with(AddAnswerServlet.class);
        serve("/changeComment").with(ChangeCommentServlet.class);
        serve("/changeUser").with(ChangeUserServlet.class);
        serve("/postsOfUser").with(PostsOfUserServlet.class);
        serve("/changePost").with(ChangePostServlet.class);
        serve("/getPublicUser").with(GetPublicUserServlet.class);
        serve("/changeEmail").with(ChangeEmailServlet.class);
        serve("/findUser").with(FindUserServlet.class);
        serve("/findPost").with(FindPostServlet.class);
//
//        serve("/filters").with(FiltersServlet.class);
//        serve("/hello").with(HelloServlet.class);
//        serve("/dbout").with(DbServlet.class);//register
//          serve("/addcar").with(AddCarServlet.class);
//          serve("/editcar").with(EditCarServlet.class);//deletecar
//        serve("/deletecar").with(DeleteCarServlet.class);
//        serve("/gethash").with(HashServlet.class);
//        serve("/profile").with(ProfileServlet.class);
//        serve("/confirm/").with(ConfirmEmailServlet.class);
    }
}
