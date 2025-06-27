# Get Deep App 💜

Um aplicativo Android baseado no jogo de cartas "Let's Get Deep" para casais se conhecerem melhor através de perguntas íntimas e divertidas.

## 📱 Sobre o App

O **Get Deep App** é uma versão digital do popular jogo de cartas que ajuda casais a se conectarem em um nível mais profundo. O app oferece três categorias de perguntas:

- **🧊 Ice Breaker**: Perguntas leves e divertidas para esquentar
- **🌊 Deep**: Perguntas mais pessoais para se conhecerem melhor
- **💕 Deeper**: Perguntas íntimas para casais prontos para ir mais fundo

## ✨ Funcionalidades

- **Interface moderna**: Design inspirado no jogo original com gradientes roxos
- **Navegação intuitiva**: Fácil transição entre telas e categorias
- **Cartas interativas**: Animação de flip para revelar o lado da resposta
- **Perguntas autênticas**: Todas as perguntas extraídas do jogo original
- **Experiência imersiva**: Gestos de toque e arraste para interação
- **Modo aleatório**: Botão para gerar perguntas aleatórias

## 🛠️ Tecnologias Utilizadas

- **Kotlin**: Linguagem principal
- **Jetpack Compose**: UI moderna e declarativa
- **Material Design 3**: Componentes e temas modernos
- **Animações**: Transições suaves entre estados
- **Architecture Components**: Estrutura robusta e escalável

## 🚀 Como Executar

1. **Clone o repositório**:
```bash
git clone git@github.com:digulino/get-deep-app.git
cd get-deep-app
```

2. **Abra no Android Studio**:
    - Importe o projeto no Android Studio
    - Sincronize o Gradle
    - Execute em um dispositivo ou emulador

3. **Requisitos**:
    - Android Studio Arctic Fox ou superior
    - Android SDK 24+
    - Kotlin 1.8+

## 📂 Estrutura do Projeto

```
app/
├── src/main/java/com/example/getdeepapp/
│   ├── MainActivity.kt          # Atividade principal
│   ├── HomeScreen.kt            # Tela inicial com instruções
│   ├── CategoryScreen.kt        # Seleção de categorias
│   ├── GameScreen.kt            # Tela do jogo com perguntas
│   └── ui/theme/
│       ├── Theme.kt             # Definição de temas
│       └── Type.kt              # Tipografia
├── src/main/res/
│   ├── values/
│   │   └── strings.xml          # Recursos de string
│   └── xml/
└── src/main/AndroidManifest.xml
```

## 🎮 Como Jogar

1. **Inicie o jogo**: Toque em "START GAME" na tela inicial
2. **Escolha uma categoria**: Ice Breaker, Deep ou Deeper
3. **Leia a pergunta**: Visualize a pergunta na carta
4. **Revele a resposta**: Toque ou arraste para virar a carta
5. **Responda**: Compartilhe seus pensamentos com seu parceiro
6. **Continue**: Use os botões Anterior/Próximo ou o botão aleatório

## 🎨 Design

O app mantém a identidade visual do jogo original:
- **Cores principais**: Gradientes roxos (#6B46C1 → #9333EA)
- **Categorias temáticas**:
    - Verde para Ice Breaker (#34D399)
    - Azul para Deep (#60A5FA)
    - Rosa para Deeper (#EC4899)
- **Tipografia**: Clean e legível
- **Animações**: Suaves e intuitivas

## 📝 Categorias de Perguntas

### Ice Breaker (27 perguntas)
Perguntas leves como "Fall or spring?", "Chocolate or vanilla?", "What's your favorite season?"

### Deep (27 perguntas)
Perguntas mais profundas como "What does your family value most?", "Where do you see yourself in 5 years?"

### Deeper (54 perguntas)
Perguntas íntimas como "What first attracted you to me?", "What's your favorite kind of foreplay?"

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:

1. Fazer fork do projeto
2. Criar uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abrir um Pull Request

## 📄 Licença

Este projeto é uma implementação não-oficial baseada no jogo "Let's Get Deep" dos criadores de "What Do You Meme?".

## 🌟 Agradecimentos

- Jogo original "Let's Get Deep" pelos criadores de "What Do You Meme?"
- Comunidade Android e Jetpack Compose
- Material Design 3 guidelines

---

**#LetsGetDeep** 💜

*"Time to put down your phone and learn about who you spend 99% of your time with."*