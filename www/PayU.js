cordova.define("cordova-plugin-payu.PayU", function(require, exports, module) {
    var exec = require('cordova/exec');
    
    exports.coolMethod = function (arg0, success, error) {
        exec(success, error, 'PayU', 'coolMethod', [arg0]);
    };
    exports.init = function(arg0,arg1,arg2,arg3,arg4,arg5,success,error){
        exec(success, error, 'PayU','init',[arg0,arg1,arg2,arg3,arg4,arg5]);
    }
    
    exports.startPayment = function(arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,
        arg12,arg13,arg14,arg15,arg16,arg17,success,error){
        exec(success, error, 'PayU','startPayment',[arg0,arg1,arg2,arg3,arg4,arg5,arg6,arg7,arg8,arg9,arg10,arg11,
            arg12,arg13,arg14,arg15,arg16,arg17])
    }
    exports.setHash = function(arg0,arg1,success,error){
        exec(success, error, 'PayU','setHash',[arg0,arg1])
    }
    });
    