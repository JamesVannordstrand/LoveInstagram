var express = require('express');
var router = express.Router();

/* GET refresh token from instagram used to exchange for an accessCode. */
router.get('/', function(req, res) {
  var url = require('url');
  var refreshCode = url.parse(req.url, true).query.code;
	res.render('users', {refreshCode: refreshCode});
});

module.exports = router;