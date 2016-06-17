# popwatch
Because Cinemoose was taken.

## API Keys:
Add your own API keys to the file `app/api.gradle`:

    android {
        buildTypes.each {
            it.buildConfigField 'String', 'THE_MOVIE_DATABASE_API_KEY', '"TMDB API key here"'
            it.buildConfigField 'String', 'YOUTUBE_DATA_API_KEY', '"YouTube API key here"'
        }
    }

Be sure to include both single and double quotes around the key.
