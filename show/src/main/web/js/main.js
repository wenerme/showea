//the require library is configuring paths
require.config({
	paths: {
		//tries to load jQuery from Google's CDN first and falls back
		//to load locally
		"jquery": ["//ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min", "libs/jquery/jquery"]
		, bootstrap: ['//netdna.bootstrapcdn.com/bootstrap/3.3.3/js/bootstrap.min', "libs/bootstrap/bootstrap"]
		, moment: ["libs/moment/moment"]
	},
	shim: {
		'bootstrap': {
			deps: ['jquery']
		}
	},
	//how long the it tries to load a script before giving up, the default is 7
	waitSeconds: 10
});
//requiring the scripts in the first argument and then passing the library namespaces into a callback
//you should be able to console log all of the callback arguments
require(['jquery', 'moment', 'app'], function ($, moment, App)
{
	console.log(App);
});