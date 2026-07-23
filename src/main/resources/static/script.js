const fileInput = document.getElementById("fileInput");
const uploadButton = document.getElementById("uploadButton");
const documentsList = document.getElementById("documentsList");
const selectedFileName = document.getElementById("selectedFileName");

const chatBox = document.getElementById("chatBox");
const questionInput = document.getElementById("questionInput");
const sendButton = document.getElementById("sendButton");
const clearChatButton = document.getElementById("clearChatButton");
const sourcesList = document.getElementById("sourcesList");

document.addEventListener("DOMContentLoaded", () => {
    loadDocuments();

    uploadButton.addEventListener("click", uploadDocument);
    sendButton.addEventListener("click", sendQuestion);
    clearChatButton.addEventListener("click", clearChat);

    questionInput.addEventListener("keydown", event => {
        if (event.key === "Enter") {
            sendQuestion();
        }
    });

    fileInput.addEventListener("change", () => {
        const file = fileInput.files[0];

        if (!file) {
            selectedFileName.textContent = "No file selected";
            return;
        }

        selectedFileName.textContent = file.name;
    });
});

async function loadDocuments() {
    try {
        documentsList.innerHTML = `<p class="empty-state">Loading documents...</p>`;

        const response = await fetch("/api/documents");

        if (!response.ok) {
            await showError(response);
            return;
        }

        const docs = await response.json();

        documentsList.innerHTML = "";

        if (docs.length === 0) {
            documentsList.innerHTML = `<p class="empty-state">No documents uploaded yet.</p>`;
            return;
        }

        docs.forEach(doc => {
            const card = document.createElement("div");
            card.className = "document-card";

            const info = document.createElement("div");

            const title = document.createElement("h3");
            title.textContent = doc.filename;

            const chunks = document.createElement("p");
            chunks.textContent = `${doc.chunksCount} chunk${doc.chunksCount === 1 ? "" : "s"}`;

            info.appendChild(title);
            info.appendChild(chunks);

            const actions = document.createElement("div");
            actions.className = "document-actions";

            const openButton = document.createElement("button");
            openButton.type = "button";
            openButton.textContent = "Open";
            openButton.addEventListener("click", () => {
                openDocumentFile(doc.id);
            });

            const deleteButton = document.createElement("button");
            deleteButton.type = "button";
            deleteButton.textContent = "Delete";
            deleteButton.className = "danger";
            deleteButton.addEventListener("click", () => {
                deleteDocument(doc.id);
            });

            actions.appendChild(openButton);
            actions.appendChild(deleteButton);

            card.appendChild(info);
            card.appendChild(actions);

            documentsList.appendChild(card);
        });
    } catch (error) {
        documentsList.innerHTML = `<p class="empty-state">Failed to load documents.</p>`;
        console.error(error);
    }
}

async function uploadDocument() {
    const file = fileInput.files[0];

    if (!file) {
        alert("Please choose a PDF file first.");
        return;
    }

    if (!file.name.toLowerCase().endsWith(".pdf")) {
        alert("Only PDF files are supported.");
        return;
    }

    const formData = new FormData();
    formData.append("file", file);

    try {
        setUploadState(true);

        const response = await fetch("/api/documents/upload", {
            method: "POST",
            body: formData
        });

        if (!response.ok) {
            await showError(response);
            return;
        }

        fileInput.value = "";
        selectedFileName.textContent = "No file selected";
        await loadDocuments();

        addMessage(`Uploaded document: ${file.name}`, "assistant");
    } catch (error) {
        alert("Failed to upload document.");
        console.error(error);
    } finally {
        setUploadState(false);
    }
}

function openDocumentFile(id) {
    window.open(`/api/documents/${id}/file`, "_blank");
}

async function deleteDocument(id) {
    const confirmed = confirm("Delete this document?");

    if (!confirmed) {
        return;
    }

    try {
        const response = await fetch(`/api/documents/${id}`, {
            method: "DELETE"
        });

        if (!response.ok) {
            await showError(response);
            return;
        }

        await loadDocuments();
        clearSources();
    } catch (error) {
        alert("Failed to delete document.");
        console.error(error);
    }
}

async function sendQuestion() {
    const question = questionInput.value.trim();

    if (!question) {
        return;
    }

    addMessage(question, "user");
    questionInput.value = "";

    const thinkingMessage = addMessage("Thinking...", "assistant thinking");

    try {
        setChatState(true);

        const response = await fetch("/api/chat", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ question })
        });

        if (!response.ok) {
            thinkingMessage.remove();
            await showError(response);
            return;
        }

        const result = await response.json();

        thinkingMessage.textContent = result.answer;
        thinkingMessage.className = "message assistant";

        renderSources(result.sources);
    } catch (error) {
        thinkingMessage.remove();
        alert("Failed to send question.");
        console.error(error);
    } finally {
        setChatState(false);
    }
}

function addMessage(text, type) {
    const message = document.createElement("div");
    message.className = `message ${type}`;
    message.textContent = text;

    chatBox.appendChild(message);
    chatBox.scrollTop = chatBox.scrollHeight;

    return message;
}

function renderSources(sources) {
    sourcesList.innerHTML = "";

    if (!sources || sources.length === 0) {
        sourcesList.innerHTML = "<p>No sources found.</p>";
        return;
    }

    const uniqueSources = [];

    sources.forEach(source => {
        const alreadyExists = uniqueSources.some(existing =>
            existing.filename === source.filename &&
            existing.chunkIndex === source.chunkIndex
        );

        if (!alreadyExists) {
            uniqueSources.push(source);
        }
    });

    uniqueSources.forEach(source => {
        const sourceElement = document.createElement("p");
        sourceElement.textContent = `${source.filename} — chunk ${source.chunkIndex}`;
        sourcesList.appendChild(sourceElement);
    });
}

function clearChat() {
    chatBox.innerHTML = `
    <div class="message assistant">
      Upload PDFs and ask questions about them.
    </div>
  `;

    clearSources();
}

function clearSources() {
    sourcesList.innerHTML = "<p>No sources yet.</p>";
}

async function showError(response) {
    try {
        const error = await response.json();
        alert(error.message || "Request failed");
    } catch {
        alert("Request failed");
    }
}

function setUploadState(isLoading) {
    uploadButton.disabled = isLoading;
    fileInput.disabled = isLoading;
    uploadButton.textContent = isLoading ? "Uploading..." : "Upload PDF";
}

function setChatState(isLoading) {
    sendButton.disabled = isLoading;
    questionInput.disabled = isLoading;
    sendButton.textContent = isLoading ? "Thinking..." : "Send";
}