import { ElementRef } from '@angular/core';

const SELECTION_RADIUS_MIN = 64;
const PROFILE_PIC_DIMENSION = 256;

let selectionInstance: PictureSelection;

class MousePos {
  public x: number;
  public y: number;

  constructor(x: number, y: number) {
    this.x = x;
    this.y = y;
  }
}

export class PictureSelection {
  public x: number;
  public y: number;
  public r: number;

  private startX: number;
  private startY: number;

  private active: boolean;
  private resize: boolean;

  private selectionStartX: number;
  private selectionStartY: number;
  private selectionStartR: number;

  private canvasRef: HTMLCanvasElement;
  private ctx: CanvasRenderingContext2D;

  private baseImageReady: boolean;
  private baseImage: HTMLImageElement = new Image();

  private MAX_SELECTED_IMG_HEIGHT: number;
  private MAX_SELECTED_IMG_WIDTH: number;

  constructor(MAX_SELECTED_IMG_WIDTH: number, MAX_SELECTED_IMG_HEIGHT: number) {
    this.MAX_SELECTED_IMG_WIDTH = MAX_SELECTED_IMG_WIDTH;
    this.MAX_SELECTED_IMG_HEIGHT = MAX_SELECTED_IMG_HEIGHT;
    this.x = 0;
    this.y = 0;
    this.r = 0;
    this.startX = 0;
    this.startY = 0;
    this.active = false;
    this.resize = false;
    this.selectionStartX = 0;
    this.selectionStartY = 0;
    this.selectionStartR = 0;
    this.baseImageReady = false;
    const selection: PictureSelection = this;
    this.baseImage.onload = function() {
      selection.baseImageReady = true;

      const widthRatio: number = selection.MAX_SELECTED_IMG_WIDTH /
        selection.baseImage.width;
      const heightRatio: number = selection.MAX_SELECTED_IMG_HEIGHT /
        selection.baseImage.height;
      let scaleFactor = 1;
      if (widthRatio < 1 || heightRatio < 1) {
        scaleFactor = Math.min(widthRatio, heightRatio);
      }

      selection.canvasRef.width = selection.baseImage.width * scaleFactor;
      selection.canvasRef.height = selection.baseImage.height * scaleFactor;
      selection.x = selection.canvasRef.width / 2;
      selection.y = selection.canvasRef.height / 2;
      selection.r = Math.min(selection.canvasRef.width, selection.canvasRef.height) / 4;
      selection.drawSelection(selection.canvasRef);
    };
    selectionInstance = this;
  }

  public setCanvasRef(canvasRef: ElementRef) {
    this.ctx = canvasRef.nativeElement.getContext('2d');
    this.canvasRef = (<HTMLCanvasElement> canvasRef.nativeElement);
    const selection: PictureSelection = this;
    this.canvasRef.addEventListener('mousedown', selection.startSelectionHelper, false);
    this.canvasRef.addEventListener('mouseup', selection.endSelectionHelper, false);
    this.canvasRef.addEventListener('mousemove', selection.mutateSelectionHelper, false);
    this.canvasRef.addEventListener('mouseout', selection.endSelectionHelper, false);
  }

  public loadImage(input) {
    if (input.files && input.files[0]) {
      const reader: FileReader = new FileReader();
      const selection: PictureSelection = this;

      reader.onload = function(e: any) {
        selection.baseImage.src = e.target.result;
      };
      reader.readAsDataURL(input.files[0]);
      input.value = null;
    }
  }

  public drawSelection(canvasRef: HTMLCanvasElement) {
    this.drawImage(canvasRef);
    const ctx: CanvasRenderingContext2D = canvasRef.getContext('2d');
    ctx.beginPath();
    ctx.arc(this.x, this.y, this.r, 0, 2 * Math.PI);
    ctx.lineWidth = 2;
    ctx.strokeStyle = 'rgba(50,50,50,0.7)';
    ctx.fillStyle = 'rgba(210,210,210,0.5)';
    ctx.fill();
    ctx.stroke();
  }

  private getMousePos(canvas, evt): MousePos {
    const rect = canvas.getBoundingClientRect();
    return new MousePos(evt.clientX - rect.left, evt.clientY - rect.top);
  }

  private startSelectionHelper(e) {
    selectionInstance.startSelection(e);
  }

  private startSelection(e) {
    const mousePos: MousePos = this.getMousePos(this.canvasRef, e);
    this.startX = mousePos.x;
    this.startY = mousePos.y;
    const dx: number = this.startX - this.x;
    const dy: number = this.startY - this.y;
    this.selectionStartX = this.x;
    this.selectionStartY = this.y;
    this.selectionStartR = this.r;

    // If we are inside the circle.
    if (dx * dx + dy * dy <= this.r * this.r) {
      this.active = true;
      this.resize = false;
    } else {
      this.resize = true;
      this.active = true;
    }
  }

  private endSelectionHelper(e) {
    selectionInstance.endSelection(e);
  }

  private endSelection(e) {
    selectionInstance.resize = false;
    selectionInstance.active = false;
  }

  private mutateSelectionHelper(e) {
    selectionInstance.mutateSelection(e);
  }

  private resizeSelection(e) {
    this.r = this.sanitizeNewRadius(this.getNewRadius(e));
    this.drawSelection(this.canvasRef);
  }

  private mutateSelection(e) {
    if (this.active && !this.resize) {
      this.transposeSelection(e);
    } else if (this.active && this.resize) {
      this.resizeSelection(e);
    }
  }

  public clipSelection() {
    this.ctx.save();
    this.drawImage(this.canvasRef);
    // Create canvas to save to.
    const writeCanvas: HTMLCanvasElement = document.createElement('canvas');
    writeCanvas.width = 2 * this.r;
    writeCanvas.height = 2 * this.r;
    const imgData: ImageData = this.ctx.getImageData(this.x - this.r, this.y - this.r, 2 * this.r, 2 * this.r);
    // Get image data.
    writeCanvas.getContext('2d').putImageData(imgData, 0, 0);
    // Write image data.
    this.resizeImg(writeCanvas);
    this.ctx.restore();
    this.drawSelection(this.canvasRef);
  }

  resizeImg(originalCanvas) {
    const newImage: HTMLImageElement = new Image();
    let outCanvas: HTMLCanvasElement = null;

    newImage.onload = function() {
      const resizeCanvas: HTMLCanvasElement = document.createElement('canvas');
      resizeCanvas.width = PROFILE_PIC_DIMENSION;
      resizeCanvas.height = PROFILE_PIC_DIMENSION;
      resizeCanvas.getContext('2d').drawImage(newImage, 0, 0,
          PROFILE_PIC_DIMENSION, PROFILE_PIC_DIMENSION);
      outCanvas = resizeCanvas;
      (<HTMLImageElement>document.getElementById('confirmSelectionImg')).src = outCanvas.toDataURL('image/png');
    };
    newImage.src = originalCanvas.toDataURL('image/png');
  }

  private drawImage(cnv) {
    if (this.baseImageReady) {
      cnv.getContext('2d').drawImage(this.baseImage, 0, 0, cnv.width, cnv.height);
    }
  }

  private keepInRange(lo, hi, rad, val) {
    if (val - rad < lo) {
      return lo + rad;
    } else if (val + rad > hi) {
      return hi - rad;
    }
    return val;
  }

  private transposeSelection(e) {
    const mousePos: MousePos = this.getMousePos(this.canvasRef, e);
    const dx: number = mousePos.x - this.startX;
    const dy: number = mousePos.y - this.startY;
    this.x = this.keepInRange(0, this.canvasRef.width, this.r, this.selectionStartX + dx);
    this.y = this.keepInRange(0, this.canvasRef.height, this.r, this.selectionStartY + dy);
    this.drawSelection(this.canvasRef);
  }

  private getNewRadius(e) {
    const mousePos: MousePos = this.getMousePos(this.canvasRef, e);
    const dx: number = mousePos.x - this.startX;
    const dy: number = mousePos.y - this.startY;

    let start_dx: number = this.selectionStartX - this.startX;
    let start_dy: number = this.selectionStartY - this.startY;
    const origDist: number = Math.sqrt(start_dx * start_dx + start_dy * start_dy);

    start_dx = start_dx / origDist;
    start_dy = start_dy / origDist;

    const dotProduct: number = (dx * start_dx + dy * start_dy);
    return this.selectionStartR - dotProduct;
  }

  private sanitizeNewRadius(newRadius) {
    this.x = this.selectionStartX;
    this.y = this.selectionStartY;
    if (2 * newRadius > this.canvasRef.height ||
        2 * newRadius > this.canvasRef.width) {

      newRadius = Math.min(this.canvasRef.height, this.canvasRef.width) / 2;

    } else if (newRadius < SELECTION_RADIUS_MIN) {
      newRadius = SELECTION_RADIUS_MIN;
    }

    if (this.y - newRadius < 0) {
      this.y = newRadius;
    } else if (this.y + newRadius > this.canvasRef.height) {
      this.y = this.canvasRef.height - newRadius;
    }

    if (this.x - newRadius < 0) {
      this.x = newRadius;
    } else if (this.x + newRadius > this.canvasRef.width) {
      this.x = this.canvasRef.width - newRadius;
    }
    return newRadius;
  }
}
