import { ElementRef } from '@angular/core';

const SELECTION_RADIUS_MIN = 64;
const PROFILE_PIC_DIMENSION = 256;

const MAX_SELECTED_IMG_WIDTH: number = document.documentElement.clientWidth * 0.65;
const MAX_SELECTED_IMG_HEIGHT: number = document.documentElement.clientHeight * 0.85;

// selectionInstance holds the Singleton instance of PictureSelection.
let selectionInstance: PictureSelection;

/**
 * MousePos describes the position of the mouse at a given point in time.
 * It is simply an x, y coordinate.
 */
class MousePos {
  // The x - coordinate of the mouse.
  public x: number;
  // The y - coordinate of the mouse.
  public y: number;

  /**
   * Construct a MousePos at the given (x, y) coordinate.
   */
  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }

  /**
   * copy returns a copy of this mouse position.
   */
  public copy(): MousePos {
    return new MousePos(this.x, this.y);
  }
}

/**
 * Circle represents a circle to be drawn within the canvas.
 * It consits of an (x, y) point, the center, and the radius.
 */
export class Circle {
  // The x - coordinate of the center of the circle.
  public x: number;
  // The y - coordinate of the center of the circle.
  public y: number;
  // The radius of the circle.
  public r: number;

  /**
   * Construct a circle centered at (x, y) with radius r.
   */
  constructor(x: number, y: number, r: number) {
    this.x = x;
    this.y = y;
    this.r = r;
  }

  /**
   * copy returns a copy of this Circle.
   */
  public copy(): Circle {
    return new Circle(this.x, this.y, this.r);
  }

  /**
   * getImageData returns an image containing the contents of the
   * canvas, cropped to the bounding box of this circle.
   */
  public getImageData(ctx: CanvasRenderingContext2D): ImageData {
    return ctx.getImageData(this.x - this.r, this.y - this.r, 2 * this.r, 2 * this.r);
  }
}

export class PictureSelection {
  // selection indicates where the selection circle is currently located.
  public selection: Circle;
  // previousSelection stores where the selection circle was when we first
  // began this mutation.
  private previousSelection: Circle;

  // startPos indicates the location of the cursor when we first began the
  // current mutation.
  private startPos: MousePos;

  // active is true iff there is currently a mutation in progress.
  private active: boolean;
  // resize is true iff we are currently resizing the selection.
  private resize: boolean;

  // canvas references the actual HTML5 canvas element that we do the profile
  // picture selection within.
  private canvas: HTMLCanvasElement;

  // baseImageReady is true iff the image that we are performing the selection on
  // is ready to be cropped.
  private baseImageReady: boolean;

  // baseImage stores the image that the user will be selecting a profile picture from.
  private baseImage: HTMLImageElement;

  /**
   * Construct a PictureSelection object on the given canvas element.
   */
  constructor(canvasRef: ElementRef) {
    // Initialize member variables.
    this.selection = new Circle(0, 0, 0);
    this.previousSelection = new Circle(0, 0, 0);
    this.startPos = new MousePos(0, 0);
    this.active = false;
    this.resize = false;
    this.baseImageReady = false;
    this.baseImage = new Image();

    // Set up canvas and event handlers.
    this.canvas = (<HTMLCanvasElement> canvasRef.nativeElement);
    this.canvas.addEventListener('mousedown', this.startSelectionHandler, false);
    this.canvas.addEventListener('mouseup', this.endSelectionHandler, false);
    this.canvas.addEventListener('mousemove', this.mutateSelectionHandler, false);
    this.canvas.addEventListener('mouseout', this.endSelectionHandler, false);

    // selection holds this instance, to be referenced in the
    // onload function.
    const selection: PictureSelection = this;

    this.baseImage.onload = function() {
      // We can now use this image.
      selection.baseImageReady = true;
      // Scale the canvas according to the larger of the image's width and height.
      const widthRatio: number = MAX_SELECTED_IMG_WIDTH / selection.baseImage.width;
      const heightRatio: number = MAX_SELECTED_IMG_HEIGHT / selection.baseImage.height;
      const scaleFactor = Math.min(1, Math.min(widthRatio, heightRatio));
      selection.canvas.width = selection.baseImage.width * scaleFactor;
      selection.canvas.height = selection.baseImage.height * scaleFactor;

      // Center the initial selection cirle in the canvas.
      selection.selection = new Circle(selection.canvas.width / 2, selection.canvas.height / 2,
          Math.min(selection.canvas.width, selection.canvas.height) / 4);
      selection.drawSelection();
    };

    // Set the global instance of selection to this instance. This will be needed for
    // proper scoping within event handlers.
    selectionInstance = this;
  }

  /**
   * loadImage will take a file input and will read it into the canvas, via the
   * baseImage's onload function.
   */
  public loadImage(input) {
    // If we received an image:
    if (input.files && input.files[0]) {
      const reader: FileReader = new FileReader();
      const selection: PictureSelection = this;

      // Load the image uploaded into the baseImage.
      reader.onload = function(e: any) {
        selection.baseImage.src = e.target.result;
      };
      reader.readAsDataURL(input.files[0]);

      // Clear out the input (if they wish to change it later).
      input.value = null;
    }
  }

  /**
   * clipSelection will store the clipped contents of the image
   * in the provided image element.
   */
  public clipSelection(img: HTMLImageElement) {
    const ctx: CanvasRenderingContext2D = this.canvas.getContext('2d');
    // Save the canvas right now.
    ctx.save();
    // Write ONLY the image onto the canvas.
    this.drawBaseImage();
    // Create canvas to save to.
    const writeCanvas: HTMLCanvasElement = document.createElement('canvas');
    writeCanvas.width = 2 * this.selection.r;
    writeCanvas.height = 2 * this.selection.r;
    // Copy the original canvas (no selection circle), within the selected region's bounding
    // box to the second canvas.
    writeCanvas.getContext('2d').putImageData(this.selection.getImageData(ctx), 0, 0);
    // Save the canvas element's data into the given image element.
    this.saveImg(writeCanvas, img);

    // For good measure, restore the canvas to its initial state.
    ctx.restore();
    this.drawSelection();
  }

  /**
   * drawSelection will draw the current selection onto the
   * appropriate HTML5 Canvas.
   */
  private drawSelection() {
    // Draw the background image onto the rendered canvas.
    this.drawBaseImage();

    // Then, draw the selection circle around the selected area.
    const ctx: CanvasRenderingContext2D = this.canvas.getContext('2d');
    ctx.beginPath();
    ctx.arc(this.selection.x, this.selection.y, this.selection.r, 0, 2 * Math.PI);
    ctx.lineWidth = 2;
    ctx.strokeStyle = 'rgba(50,50,50,0.7)';
    ctx.fillStyle = 'rgba(210,210,210,0.5)';
    ctx.fill();
    ctx.stroke();
  }

  /**
   * getMousePos will return the position of the mouse during a given event,
   * offset within the canvas.
   */
  private getMousePos(canvas: HTMLCanvasElement, evt: MouseEvent): MousePos {
    const rect = canvas.getBoundingClientRect();
    return new MousePos(evt.clientX - rect.left, evt.clientY - rect.top);
  }

  //
  //  Event Handlers
  //      Note that these will defer to the singleton instance of
  //      PictureSelection, which fixes scoping issues with the
  //      handlers being treated as methods of the canvas.
  //

  /**
   * Handle the start of a selection (mouse pressed in the canvas).
   */
  private startSelectionHandler(e: MouseEvent) {
    selectionInstance.startSelection(e);
  }

  /**
   * Handle the end of a selection (mouse leaves or is released).
   */
  private endSelectionHandler(e: MouseEvent) {
    selectionInstance.endSelection(e);
  }

  /**
   * Handle a selection mutation (mouse is moved in the canvas).
   */
  private mutateSelectionHandler(e: MouseEvent) {
    selectionInstance.mutateSelection(e);
  }

  /**
   * startSelection will handle the start of a new selection,
   * storing the inital position of the mouse and determining
   * if the user is moving or resizing the selection.
   */
  private startSelection(e: MouseEvent) {
    // Store mouse position and location of inital selection.
    const mousePos: MousePos = this.getMousePos(this.canvas, e);
    this.startPos = mousePos.copy();
    this.previousSelection = this.selection.copy();

    const dx: number = this.startPos.x - this.selection.x;
    const dy: number = this.startPos.y - this.selection.y;

    // If we are inside the inital selection, then the user wants to
    // translate the selection. Otherwise, they wish to resize the
    // selection.
    this.resize = dx * dx + dy * dy > this.selection.r * this.selection.r;
    this.active = true;
  }

  /**
   * endSelection will stop the selection from being changed when the mouse is moved.
   */
  private endSelection(e: MouseEvent) {
    selectionInstance.resize = false;
    selectionInstance.active = false;
  }

  /**
   * mutateSelection will perform the appropriate change to the selection, if applicable.
   */
  private mutateSelection(e: MouseEvent) {
    if (this.active && !this.resize) {
      this.translateSelection(e);
    } else if (this.active && this.resize) {
      this.resizeSelection(e);
    }
  }

  /**
   * resizeSelection will handle the event in which the selection is to
   * be resized.
   */
  private resizeSelection(e: MouseEvent) {
    this.selection.r = this.sanitizeNewRadius(this.getNewRadius(e));
    this.drawSelection();
  }

  /**
   * translateSelection will handle the event in which the selection
   * is to be moved to another part of the image.
   */
  private translateSelection(e: MouseEvent) {
    // How much has the selection moved since we started the translation?
    const mousePos: MousePos = this.getMousePos(this.canvas, e);
    const dx: number = mousePos.x - this.startPos.x;
    const dy: number = mousePos.y - this.startPos.y;
    // Adjust that amount, keeping the selection inside the canvas.
    this.selection.x = this.keepInRange(0, this.canvas.width, this.selection.r,
        this.previousSelection.x + dx);
    this.selection.y = this.keepInRange(0, this.canvas.height, this.selection.r,
        this.previousSelection.y + dy);
    // Re-draw the canvas.
    this.drawSelection();
  }

  /**
   * saveImg will save the image in the canvas element into the
   * provided HTML image element, resizing it in the process.
   */
  private saveImg(originalCanvas: HTMLCanvasElement, img: HTMLImageElement) {
    const newImage: HTMLImageElement = new Image();

    // When the new image is loaded, put it into a new canvas element with the
    // desired image, then save that into the output image.
    newImage.onload = function() {
      const resizeCanvas: HTMLCanvasElement = document.createElement('canvas');
      resizeCanvas.width = PROFILE_PIC_DIMENSION;
      resizeCanvas.height = PROFILE_PIC_DIMENSION;
      resizeCanvas.getContext('2d').drawImage(newImage, 0, 0,
        PROFILE_PIC_DIMENSION, PROFILE_PIC_DIMENSION);
      img.src = resizeCanvas.toDataURL('image/png');
    };
    newImage.src = originalCanvas.toDataURL('image/png');
  }

  /**
   * drawImage will draw the baseImage on the canvas.
   */
  private drawBaseImage() {
    if (this.baseImageReady) {
      this.canvas.getContext('2d').drawImage(this.baseImage, 0, 0,
          this.canvas.width, this.canvas.height);
    }
  }

  /**
   * keepInRange takes a value, a low bound, a high bound, and the
   * minimum distance the value must be from both bounds (radius),
   * and returns the best adjusted value to satisfy the given constraints.
   */
  private keepInRange(lo: number, hi: number, rad: number, val: number) {
    if (val - rad < lo) {
      return lo + rad;
    } else if (val + rad > hi) {
      return hi - rad;
    }
    return val;
  }

  /**
   * getNewRadius will calculate what the radius of the selection should be
   * resized to after a given mouse event, without sanitizing it to ensure
   * that it should remain in bounds.
   */
  private getNewRadius(e: MouseEvent) {
    // Get the vector from initial cursor location to the current.
    const mousePos: MousePos = this.getMousePos(this.canvas, e);
    const dx: number = mousePos.x - this.startPos.x;
    const dy: number = mousePos.y - this.startPos.y;

    // Get the vector from the intial cursor location to the center of the
    // selection, then normalize it.
    let start_dx: number = this.previousSelection.x - this.startPos.x;
    let start_dy: number = this.previousSelection.y - this.startPos.y;
    const origDist: number = Math.sqrt(start_dx * start_dx + start_dy * start_dy);
    start_dx = start_dx / origDist;
    start_dy = start_dy / origDist;

    // Compute the dot product (the amount of change we have along
    // the correct axis), and change the initial radius by that much.
    const dotProduct: number = (dx * start_dx + dy * start_dy);
    return this.previousSelection.r - dotProduct;
  }

  /**
   * sanitizeNewRadius will take a radius to resize the selection to
   * and will adjust the selection to accomodate that radius.
   */
  private sanitizeNewRadius(newRadius: number) {
    this.selection.x = this.previousSelection.x;
    this.selection.y = this.previousSelection.y;

    // The diameter cannot be larger than the canvas, but the radius
    // must also be larger than the minimum size, if possible.
    if (2 * newRadius > this.canvas.height ||
        2 * newRadius > this.canvas.width) {

      newRadius = Math.min(this.canvas.height, this.canvas.width) / 2;

    } else if (newRadius < SELECTION_RADIUS_MIN) {
      newRadius = SELECTION_RADIUS_MIN;
    }

    // If the circle goes above/below the top/bottom of the canvas,
    // move it down/up accordingly.
    if (this.selection.y - newRadius < 0) {
      this.selection.y = newRadius;
    } else if (this.selection.y + newRadius > this.canvas.height) {
      this.selection.y = this.canvas.height - newRadius;
    }

    // If the selection goes left/right of the left/right of the canvas,
    // move it right/left accordingly.
    if (this.selection.x - newRadius < 0) {
      this.selection.x = newRadius;
    } else if (this.selection.x + newRadius > this.canvas.width) {
      this.selection.x = this.canvas.width - newRadius;
    }
    return newRadius;
  }
}
