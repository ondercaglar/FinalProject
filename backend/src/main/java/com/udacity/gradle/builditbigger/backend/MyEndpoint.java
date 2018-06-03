package com.udacity.gradle.builditbigger.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import javax.inject.Named;

import gradle.udacity.com.ajokesource.JokeSource;

/** An endpoint class we are exposing */
@Api(
        name = "myApi",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.builditbigger.gradle.udacity.com",
                ownerName = "backend.builditbigger.gradle.udacity.com",
                packagePath = ""
        )
)
public class MyEndpoint {

    /** A simple endpoint method that takes a name and says Hi back */
    @ApiMethod(name = "sayHi")
    public MyBean sayHi(@Named("name") String name)
    {
        MyBean response = new MyBean();
        response.setData("Hi, " + name);

        return response;
    }



    //modify the GCE starter code to pull jokes from your Java library.
    /** A simple endpoint method that pull jokse from Java Library */
    @ApiMethod(name = "pulljokes")
    public MyBean pulljokes()
    {
        JokeSource jokeSource = new JokeSource();
        String joke = jokeSource.getJoke();

        MyBean response = new MyBean();
        response.setData(joke + " via GCE");

        return response;
    }



}
