app.controller('ProfilePictureController', ['$scope', '$routeParams', '$http',
    function($scope, $routeParams, $http) {

    var MAX_SELECTED_IMG_WIDTH  = document.documentElement.clientWidth * 0.65;
    var MAX_SELECTED_IMG_HEIGHT = document.documentElement.clientHeight * 0.85;
    var PROFILE_PIC_DIMENSION = 256;
    var SELECTION_RADIUS_MIN = 64;

    var canvas = document.getElementById("imageSelectionCanvas");
    var ctx = canvas.getContext("2d");

    var baseImageReady = false;
    var baseImage = new Image();

    $scope.UPLOAD_STAGE_SELECT_FILE = 0;
    $scope.UPLOAD_STAGE_START = $scope.UPLOAD_STAGE_SELECT_FILE;
    $scope.UPLOAD_STAGE_DRAW_SELECTION = 1;
    $scope.UPLOAD_STAGE_CONFIRM_SELECTION = 2;
    $scope.UPLOAD_STAGE_FINISHED = 3;
    $scope.UPLOAD_STAGE_ERROR = 4;

    $scope.uploadStage = $scope.UPLOAD_STAGE_START;

    baseImage.onload = function () {
        baseImageReady = true;
        drawSelection(canvas);
    };

    // selection holds the area surrounded by the circle.
    selection = {
        x: 300,
        y: 700,
        r: SELECTION_RADIUS_MIN,
    }

    // TODO: Have a better means of maintaining state than $scope.uploadStage

    // Used to return to the first screen.
    $scope.returnToFileSelect = function() {
        $scope.uploadStage = $scope.UPLOAD_STAGE_SELECT_FILE;
    }

    // Used to return to the 2nd screen.
    $scope.returnToSelectRegion = function() {
        $scope.uploadStage = $scope.UPLOAD_STAGE_DRAW_SELECTION;
    }

    // Used to advance to the confirmation screen.
    // TODO: Send the request here!
    $scope.confirmSelection = function() {
        $scope.uploadStage = $scope.UPLOAD_STAGE_FINISHED;

        /*
        $http({
            method: 'PUT',
            url: '/api/profile/image/' + $routeParams.username,
            headers: {'Content-Type': undefined},
            transformRequest: angular.identity,
            data: {
                'data':  document.getElementById("confirmSelectionImg").src.replace(/^data:image\/(png|jpg);base64,/, ""),
                'mimetype': 'image/png'
            }
        }).then(function(response) {
            console.log('success');
        }, function(response) {
            console.error(response);
        }); */

        $http.put('/api/profile/image/' + $routeParams.username,{
                "data":  document.getElementById("confirmSelectionImg").src.replace(/^data:image\/(png|jpg);base64,/, ""),
                "mimetype": "image/png"
            }
        );
    }

    // loadImage is used to load the image into the selection canvas.
    $scope.loadImage = function(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();

            reader.onload = function (e) {
                baseImage.src = e.target.result;

                var widthRatio = MAX_SELECTED_IMG_WIDTH / baseImage.width;
                var heightRatio = MAX_SELECTED_IMG_HEIGHT / baseImage.height;
                var scaleFactor = 1;

                if (widthRatio < 1 || heightRatio < 1) {
                    scaleFactor = Math.min(widthRatio, heightRatio);
                }

                canvas.width = baseImage.width * scaleFactor;
                canvas.height = baseImage.height * scaleFactor;
                selection.x = canvas.width / 2;
                selection.y = canvas.height / 2;
                selection.r = Math.min(canvas.width, canvas.height) / 4;
                $scope.$apply(function() {
                    $scope.uploadStage = $scope.UPLOAD_STAGE_DRAW_SELECTION;
                });
            };
            reader.readAsDataURL(input.files[0]);
            input.value = null;
        } 
    }

    // clear resets the screen to just be the image.
    function drawImage(cnv) {
        if (baseImageReady) {
            cnv.getContext("2d").drawImage(baseImage, 0, 0, cnv.width,
                    cnv.height);
        }
    }

    // draw draws the selection image on the screen.
    function drawSelection(cnv) {
        drawImage(cnv);
        var ctx = cnv.getContext("2d");
        ctx.beginPath();
        ctx.arc(selection.x, selection.y, selection.r, 0, 2*Math.PI);
        ctx.lineWidth = 2;
        ctx.strokeStyle = 'rgba(50,50,50,0.7)';
        ctx.fillStyle= 'rgba(210,210,210,0.5)';
        ctx.fill();
        ctx.stroke();
    }

    // getMousePos returns the coordinates of the mouse within the canvas.
    function getMousePos(canvas, evt) {
        var rect = canvas.getBoundingClientRect();
        return {
            x: evt.clientX - rect.left,
            y: evt.clientY - rect.top
        };
    }

    // The initial coordinates of the mouse when clicked.
    var startX, startY;

    // Holds which "mode" of changing the image we are in.
    var active, resize;

    // Holds the initial state of the collection when first
    // selected.
    var selectionStartX, selectionStartY, selectionStartR;

    // initSelectionData prepares everything for a mutation
    // to the given selection (translating / scaling it).
    function startSelection(e) {
        mousePos = getMousePos(canvas, e);
        startX = mousePos.x;
        startY = mousePos.y;
        var dx = startX - selection.x;
        var dy = startY - selection.y;
        selectionStartX = selection.x;
        selectionStartY = selection.y;
        selectionStartR = selection.r;

        if (dx*dx + dy*dy <= selection.r * selection.r) {
            active = true;
            resize = false;
        } else {
            resize = true;
            active = false;
        }
    }

    // endSelection cleans up after a selection mutation
    // (translation / scaling) is done.
    function endSelection(e) {
        resize = false;
        active = false;
    }

    // mutateSelection moves the selection or resizes it.
    function mutateSelection(e) {
        if (active) {
            transposeSelection(e);
        } else if (resize) {
            resizeSelection(e);
        }
    }


    canvas.addEventListener("mousedown", startSelection, false);
    canvas.addEventListener("mouseup", endSelection, false);
    canvas.addEventListener("mousemove", mutateSelection, false);
    canvas.addEventListener("mouseout", endSelection, false);

    // clipSelection clips to the selected region.
    $scope.clipSelection = function() {
        ctx.save();
        drawImage(canvas);
        // Create a new canvas and put the selected image data on it
        var writeCanvas = document.createElement("canvas");
        writeCanvas.width = 2*selection.r;
        writeCanvas.height = 2*selection.r;
        var imgData = ctx.getImageData(selection.x - selection.r,
                selection.y - selection.r, 2*selection.r, 2*selection.r);
        writeCanvas.getContext("2d").putImageData(imgData, 0, 0);
        $scope.uploadStage = $scope.UPLOAD_STAGE_CONFIRM_SELECTION;

        // Resize this and render it.
        resizeImg(writeCanvas);

        ctx.restore();
        drawSelection(canvas);
    }

    // resizeImg resizes the selection such that it fits the required size.
    function resizeImg(originalCanvas) {
        var newImage = new Image();
        var outCanvas = null;

        newImage.onload = function() {
            var resizeCanvas = document.createElement("canvas");
            resizeCanvas.width = PROFILE_PIC_DIMENSION;
            resizeCanvas.height = PROFILE_PIC_DIMENSION;
            resizeCanvas.getContext("2d").drawImage(this, 0, 0,
                    PROFILE_PIC_DIMENSION, PROFILE_PIC_DIMENSION);
            outCanvas = resizeCanvas;
            document.getElementById("confirmSelectionImg").src = outCanvas.toDataURL("image/png");
        };
        newImage.src = originalCanvas.toDataURL("image/png");
    }

    // keepInRange ensures that a translation stays inside the canvas.
    function keepInRange(lo, hi, rad, val) {
        if (val - rad < lo) {
            return lo + rad;
        } else if (val + rad > hi) {
            return hi - rad;
        }
        return val;
    }

    // transposeSelection moves the selected region's circle a bit.
    function transposeSelection(e) {
        mousePos = getMousePos(canvas, e);
        var dx = mousePos.x - startX;
        var dy = mousePos.y - startY;
        selection.x = keepInRange(0, canvas.width, selection.r, selectionStartX + dx);
        selection.y = keepInRange(0, canvas.height, selection.r, selectionStartY + dy);
        drawSelection(canvas);
    }

    // getNewRadius determines what the updated radius of the selection should
    // be. This radius may be of an invalid size or go off the screen.
    function getNewRadius(e) {
        mousePos = getMousePos(canvas, e);
        var dx = mousePos.x - startX;
        var dy = mousePos.y - startY;

        var start_dx = selectionStartX - startX;
        var start_dy = selectionStartY - startY;
        var origDist = Math.sqrt(start_dx * start_dx + start_dy * start_dy);
        start_dx = start_dx / origDist;
        start_dy = start_dy / origDist;

        var dotProduct = (dx * start_dx + dy * start_dy);
        return selectionStartR - dotProduct;
    }

    // sanitizeNewRadius takes a new radius for the selection, and adjusts it
    // and the selection location so that the selection fits on the screen.
    function sanitizeNewRadius(newRadius) {
        selection.x = selectionStartX;
        selection.y = selectionStartY;

        if (2 * newRadius > canvas.height || 2 * newRadius > canvas.width) {
            newRadius = Math.min(canvas.height, canvas.width) / 2;
        } else if (newRadius < SELECTION_RADIUS_MIN) {
            newRadius = SELECTION_RADIUS_MIN;
        }

        if (selection.y - newRadius < 0) {
            selection.y = newRadius;
        } else if (selection.y + newRadius > canvas.height) {
            selection.y = canvas.height - newRadius;
        }

        if (selection.x - newRadius < 0) {
            selection.x = newRadius;
        } else if (selection.x + newRadius > canvas.width) {
            selection.x = canvas.width - newRadius;
        }
        return newRadius;
    }

    // resizes the selected area to match the specified size, and then
    // writes it to the confimation image.
    function resizeSelection(e) {
        selection.r = sanitizeNewRadius(getNewRadius(e));
        drawSelection(canvas);
    }
}]);
