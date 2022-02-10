## ☝️ Prerequisites

### 🛠️ Tools

- **IDE** e.g. [Spring Tool Suite](https://spring.io/tools)/[IntelliJ](https://www.jetbrains.com/idea/download/)
- [**Java JDK** 1.8 or higher](https://sap.github.io/SapMachine/#download)
- [**Maven**](https://maven.apache.org/)
- [**Git client**](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

{% if tools %}
{% for entry in tools %}
  - {{ entry }}
{% endfor %}
{% endif %}

### 📚 Knowledge

- Required:

    - Java
    - [Maven](https://maven.apache.org/)

  {% if required %}
  {% for entry in required %}
    - {{ entry }}
  {% endfor %}
  {% endif %}

- Beneficial:
   
    - Git

    {% if beneficial %}
    {% for entry in beneficial %}
      - {{ entry }}
    {% endfor %}
    {% endif %}
