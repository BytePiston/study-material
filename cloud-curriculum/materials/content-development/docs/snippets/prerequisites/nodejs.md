## ☝️ Prerequisites

### 🛠️ Tools

- [**Git** client](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

- [**Node.js** LTS (>=16)](https://nodejs.org/en/download/)

    - If you need to manage multiple versions of `node` &/or `npm`, consider using a [Node Version Manager](https://github.com/npm/cli#node-version-managers) or running in a [development container](https://code.visualstudio.com/docs/remote/create-dev-container#_automate-dev-container-creation) using the [VS Code Remote - Containers extension](https://marketplace.visualstudio.com/items?itemName=ms-vscode-remote.remote-containers)

- **IDE** of your choice

    - we recommend [Visual Studio Code](https://code.visualstudio.com/) with [EditorConfig](https://marketplace.visualstudio.com/items?itemName=EditorConfig.EditorConfig) and [ESLint](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) extensions

{% if tools %}
{% for entry in tools %}
  - {{ entry }}
{% endfor %}
{% endif %}

### 📚 Knowledge

- Required:
    - [Node.js](https://nodejs.org/en/docs)

    - [ECMAScript modules](https://nodejs.org/docs/latest-v16.x/api/esm.html)

    {% if required %}
    {% for entry in required %}
    - {{ entry }}
    {% endfor %}
    {% endif %}

- Beneficial:

    - [Git](https://git-scm.com/docs)

    {% if beneficial %}
    {% for entry in beneficial %}
    - {{ entry }}
    {% endfor %}
    {% endif %}
