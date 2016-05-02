package com.redbassett.popwatch.MovieApi;

import com.redbassett.popwatch.Movie;

public abstract class MovieApi {
    public abstract Movie[] getMovies(String type);

    public abstract String getMovieTrailer(long id);
}
