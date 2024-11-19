# OpenAI with VectorDB (Swing GUI)

A Java application that combines OpenAI's GPT API with a vector database for finding nearest neighbors and querying responses. The app provides a simple GUI built using Swing.

## Features
- **Nearest Neighbor Search:** 
  - Use cosine similarity to find the nearest neighbor for a given query vector in the SQLite database.
- **OpenAI Integration:** 
  - Send a text prompt to OpenAI's GPT model and display the generated response.
- **Swing GUI:** 
  - Intuitive user interface with input fields, buttons, and an output area.

## How This Implements Retrieval-Augmented Generation (RAG)
Retrieval-Augmented Generation (RAG) is a technique that combines external knowledge retrieval with a generative model to provide context-aware responses. In this project:

1. **Database as Knowledge Retrieval**:
   - The SQLite database acts as the retrieval component. It stores content and its associated vector representations.
   - When a query vector is entered, the application searches for the nearest neighbor using cosine similarity. This is equivalent to retrieving relevant knowledge based on the query.

2. **Generative Model (OpenAI GPT)**:
   - The OpenAI GPT model is the generative component. It processes the user's prompt and generates a response based on the input.
   - The retrieved content from the vector database can be integrated into the prompt to provide additional context for the generation process.

3. **RAG Workflow**:
   - The user inputs a query vector, and the nearest neighbor (relevant content) is retrieved from the database.
   - Optionally, the user can use the retrieved content to enrich their OpenAI prompt, allowing the generative model to produce a more contextually relevant response.

### Example of RAG in Action
1. **Query Vector**:
   - Input: `0.1,0.2,0.3,0.4`.
   - The application retrieves: `Example Content`.

2. **OpenAI Prompt with Retrieved Content**:
   - Prompt: "Using the information: 'Example Content', explain its significance."
   - The OpenAI GPT model generates a response using the retrieved content as context.

3. **Output**:
   - `"The significance of 'Example Content' lies in ..."`

This RAG-like approach ensures that responses are tailored based on relevant external knowledge.

---

## Requirements
- Java 8 or later
- SQLite database (`Vector.db`)
- OpenAI API key
- Maven for dependencies (optional)

## How to Run
1. Clone the repository.
   ```bash
   git clone https://github.com/yourusername/yourproject.git
   cd yourproject
