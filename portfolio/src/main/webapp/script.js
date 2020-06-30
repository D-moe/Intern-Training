// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License"); you may not
// use this file except in compliance with the License. You may obtain a copy of
// the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// License for the specific language governing permissions and limitations under
// the License.

window.onload = (event) => {
  SetImageOnMouseOver();
  loadComments();
};
/**
 * Change the given image to a random one whenever the user hovers over
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
 *
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
  // in the directory For now copy directly the example code with minor
  // modifications.

  var randomFile =
      path + fileName + (Math.floor(Math.random() * numElems) + 1) + '.jpg';
  element.src = randomFile;
}
/**
 *
 * @param {string} path - the path the given query refers to
 * @param {DOM element} element - an element in the DOM for selecting
 */
function fetchAndModify(path, element) {
  fetch(path)
      .then(response => response.text())
      .then(content => element.innerHTML = content)
}
/*
 * This code creates an innerHTML and then reads its text to remove special
 * characters from the input
 * @param {string} str The input to remove special characters from to prevent
 *     scripting attacks
 */
var sanitizeHTML = function(str) {
  var temp = document.createElement('div');
  temp.textContent = str;
  return temp.innerHTML;
};


/*
 * This function fetches the value return by the given data service and then
 * modifies the content of a div to store each of the response values as a
 * paragraph.
 * @param {string} path The url of the data service to use
 */
function fetchAndChangeBody(path) {
  fetch(path).then(response => response.json()).then(json => {
    let index = 0;
    for (index in json) {
      console.log(json[index]);
      const target = document.getElementById('comment');
      let newComment = document.createElement('p');
      newComment.innerText = json[index];
      target.appendChild(newComment);
      index++;
    }
  })
}

/*
 * This function fetches the comment data and adds each of them to the loaded
 * content.
 * TODO(morleyd): change individual comment load to be asynchronous to improve
 * performance. It might also make sense to have a hard limit on the number of
 * comments loaded, think about this for future implementations.
 */
function loadComments() {
  fetch('\data')
      .then(response => response.json())
      .then(
          json => {
            console.log(json)
            var index = 0;
            for (index in json) {
              let userName = json[index]['userName'];
              console.log(userName)
              let commentBody = json[index]['commentBody'];
              addComment(/* name= */ userName, /* commentBody= */ commentBody);
            }
          }

      )
}

/**
 * A helper that creates the divs for a new comment, intended to be called by
 * loadComments.
 * @param {string} name The name of the user who made the comment
 * @param {string} content The body of the comment
 */
function addComment(name, content) {
  const target = document.getElementById('prior-comments');
  let newComment = document.createElement('div');
  let body = newComment.appendChild(document.createElement('div'));
  body.className = 'comment-body'
  let user = newComment.appendChild(document.createElement('div'));
  user.className = 'sub-script'
  // We need to make sure to strip comments of any special chars they may have
  // in order to prevent injection attacks
  user.innerText = '-' + sanitizeHTML(name);
  body.innerText = sanitizeHTML(content);
  target.appendChild(newComment);
}