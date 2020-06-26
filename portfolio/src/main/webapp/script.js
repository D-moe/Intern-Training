window.onload = SetImageOnMouseOver;

/**
 * Change the given image to a random one whenever the user hovers over.
 */
function SetImageOnMouseOver() {
  document.getElementById('rand-front-tile')
      .addEventListener('mouseover', function() {
        getRandomFile(
            /* element= */ document.querySelector('#rand-back-tile img'),
            /* path= */ 'images/random/', /* fileName= */ 'test',
            /* numElems= */ 4)
      });
}

/**
 * Select a random file from a given path and change the given element src to
 * that file.
 * @param {string} element - which DOM element to modify
 * @param {string} path - the path starting from the webapp/ directory that the
 *     files are located in
 * @param {string} fileName - the startPrefix of the files in this directory
 *     (assumed all files of the form fileNameXX.jpg)
 * @param {int} numElems - the number of elements in the given directory
 */
function getRandomFile(element, path, fileName, numElems) {
  // It would be ideal if files didn't have to follow a certain naming
  // convention, but not sure this is possible with strictly server side
  // JavaScript. After conferring with help line seems this is indeed
  // impossible.
  // TODO(morleyd): Implement on server side to clean up need for numElems, etc
  // in the directory. For now copy directly the example code with minor
  // modifications.
  const computedIndex = (Math.floor(Math.random() * numElems) + 1);
  const randomFile = path + fileName + computedIndex + '.jpg';
  element.src = randomFile;
}
