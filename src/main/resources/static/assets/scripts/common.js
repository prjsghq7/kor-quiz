/**
 * @return {HTMLElement}
 */
HTMLElement.prototype.hide = function () {
    this.classList.remove('-visible');
    return this;
}

/**
 * @return {HTMLElement}
 */
HTMLElement.prototype.show = function () {
    this.classList.add('-visible');
    return this;
}