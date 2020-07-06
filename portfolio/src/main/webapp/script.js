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
  setImageOnMouseOver();
  loadComments('/data');
};
/**
 * Change the given image to a random one whenever the user hovers over.
 */
function setImageOnMouseOver() {
  document.getElementById('rand-front-tile')
      .addEventListener('mouseover', function() {
        getRandomFile(
            /* element= */ document.querySelector('#rand-back-tile img'),
            /* path= */ 'images/random/', /* fileName= */ 'test',
            /* numElems= */ 4)
      });
}
/**
 * Change the visibility of the comment fields so the user can add a new
 * comment.
 */
function showComment() {
  document.getElementById('add-comment').style.visibility = 'visible';
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
/**
 * Creates an innerHTML element and then reads its text to remove special
 * characters from the input
 * @param {string} str The input to remove special characters from to prevent
 *     scripting attacks
 */
function sanitizeHtml(str) {
  let temp = document.createElement('div');
  temp.textContent = str;
  return temp.innerHTML;
};


/**
 * Fetches the value return by the given data service and then
 * modifies the content of a div to store each of the response values as a
 * paragraph.
 * @param {string} path The url of the data service to use
 */
function fetchAndChangeBody(path) {
  fetch(path).then(response => response.json()).then(json => {
    const target = document.getElementById('comment');
    let newComment = document.createElement('p');
    for (index in json) {
      newComment.innerText = json[index];
      target.appendChild(newComment);
    }
  })
}

/**
 * Fetches the comment data and adds each of them to the loaded
 * content.
 * TODO(morleyd): change individual comment load to be asynchronous to improve
 * performance. It might also make sense to have a hard limit on the number of
 * comments loaded, think about this for future implementations.
 * @param {string} path The path to use when fetching data
 */
function loadComments(path) {
  fetch(path)
      .then(response => response.json())
      .then(
          json => {
            console.log('Load comment is now running');
            console.log('The json is');
            console.log(json);
            for (index in json) {
              let userName = json[index]['userName'];
              let commentBody = json[index]['commentBody'];
              addComment(/* name= */ userName, /* content= */ commentBody);
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
  user.innerText = '-' + sanitizeHtml(/* str= */ name);
  body.innerText = sanitizeHtml(/* str= */ content);
  target.appendChild(newComment);
}
/**
 * Clear all generated comments from database and refetch.
 */
function clearComments() {
  fetch('/deletedata', {
    method: 'POST',
    headers: {'Content-Type': 'application/json;charset=utf-8'},
    body: ''
  })
      .then(() => {
        clearBody();
      })
      .then(() => {
        loadComments('/data?refresh=true');
      })
}

/**
 * Clear body of the comments div, removing all prior comments
 */
function clearBody() {
  let body = document.querySelectorAll('.comment-body');
  console.log('The body is');
  console.log(body);
  let names = document.querySelectorAll('.sub-script');
  for (const commentBody of body) {
    console.log(commentBody);
    commentBody.parentNode.removeChild(commentBody);
  }
  for (const userName of names) {
    userName.parentNode.removeChild(userName);
  }
}
