const response = await fetch("localhost:8080/allUsers");
const jsonData = await response.json();

let app = document.getElementById("btn1");

for (const user of jsonData) {

    let img = document.createElement("img");
    img.setAttribute("src", "/media/" + user["avatar"]);
    img.setAttribute("alt", "none");

    let h1 = document.createElement("h1");
    h1.innerText = user["login"];

    let el = document.createElement("div");
    el.append(h1);
    el.append(img);
    app.append(el);
}