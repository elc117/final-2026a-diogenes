## 1. Identificação

- Nome: Diógenes Potrich Steca
- Curso: Sistemas de Informação

---
## 2. Proposta

O objetivo desse trabalho é desenvolver um jogo de batalha em turnos estilo roguelike, desenvolvido em java com a biblioteca LIBGDX. O jogador controla uma matilha de três lobos(Alfa, Espiritualista e Rastreador) com papéis distintos que precisam sobreviver 7 dias de inverno, caçando outras ameaças animais e gerenciando recursos.
Como funcionalidades importantes de se pontuar temos:
- O sistema de habilidades, cada lobo tendo duas habilidades únicas que aproveitam polimorfismo por meio de uma `Skill` base.
- O sistema de turnos, que toda unidade(`Unit`) age de acordo com seu atributo de velocidade(`Speed`), quanto maior antes a unidade agirá, o jogador escolhendo uma habilidade e um alvo e os inimigos controlados por heurísticas.
- O sistema de efeitos de status(`statusEffects`), que são aplicados no meio de combate onde algumas habilidades os aplicam e afetam a batalha e os estados das unidades.
- O sistema de campanha de 7 dias, que gerencia a progressão do jogador com dificuldade progressiva.
- O sistema de gerador de encontros(`encounterGenerator`), que conjuntamente com a campanha garante variabilidade entre os encontros de cada dia, assim diversificando e enriquecendo a expêriencia do jogador.
- O Leaderboard, que persiste os dados da gameplay, ao fim dela, dos jogadores e os exporta pra um outro ambiente, salvando informações-chave.

Por fim, como proposto pela professora, o trabalho é versionado e desenvolvido de maneira incremental, a maneira que preferi desenvolver foi primeiro o nucleo lógico(core), como classes puramente escritas em java, e depois integrações com a interface e ferramentas do Libgdx. Funcionalidades adicionais possívelmente consideradas no momento de desenvolvimento foram avaliadas dado escopo e tempo restante.

---
## 3. Processo de desenvolvimento 

Sobre a proposta inicial: o core está, de certa forma, como imaginei, mas bastante coisa foi reconsiderada/refatorada conforme o desenvolvimento dado os recursos disponíveis e o tempo, abaixo explico um pouco o que foi cortado e a mudança de direção.

o corte de features: a ideia principal, inclusive que imaginei, era termos níveis e eles serem uma direção de progressão dentro do jogo juntamente com o treinamento no acampamento(fase entre as batalhas), mas após implementar o cooldown e os níveis de forma básica ponderei se realmente valeriam a pena pois adicionariam muita abstração tanto pra mim(no sentido de implementar) quanto pro jogador(pra entender as mecanicas)
dado isso resolvi deixar apenas uma maneira de progredir no jogo e tambem mudei o projeto e minha mentalidade pra uma visão "completar o necessario(core) primeiro" e então reduzi bastante o previsto de 4 skills por lobo para 2 apenas mas cada um com sua função bem definida e a mesma coisa para os efeitos de status e inimigos, ambos enxutos. Ainda, dado o tempo curto não consegui implementar uma parte que explica o que cada inimigo faz(um monstruario ou enciclopedia) e nem a parte de eventos que seriam conjuntamente com o acampamento e adicionariam mais variabilidade ao jogo

separação de responsabilidades: fiz grande uso de "managers" e estruturas "model"(ou entities), onde são classes puramente javas que manipulam dados e apenas fazem o que estão no alcance delas, deixando a camada bruta limpa e mais tarde desenvolvi as "screens" que elas manipulam os managers e as funcionalidades dentro deles pra gerar o jogo efetivamente.

problemas com skills e varios alvos: minha implementação inicial de skills apenas levava um target como parametro, isso dava problema pois no escopo inicial já tinha pensado em incluir habilidades em area(ou que aplicam em multiplos alvos), resolvi isso usando sobrecarga de metodos com um metodo ``execute`` que recebe apenas um alvo e outro que recebe uma lista de alvos.

polimorfismo, herança e sobrescrita de metodos: o que eu tinha desde o começo em mente e foi algo que perdurou ate a ideia final do projeto era fazer uma estrutura robusta de herança, foi assim com Unit->Wolf e Enemy-> e desses dois herdavam os especificos implementados(Lobos e inimigos, de fato), já as skills, que tambem utilizam polimorfismo, utilizam a sobrescrita de metodo atráves de um metodo padrão(abstrato) que não faz nada na classe abstrata e elas proprias implementam suas lógicas em seus métodos

uso de wildcards(genericos): quando estava desenvolvendo a mecanica de turnos acabei usando algumas funcoes helpers que processavam ambos inimigos e lobos, porem achei um problema que ao passar uma List<Wolf> não é considerada uma List<Unit> por exemplo, pesquisei mais a fundo e vi que os generics são invariantes, resolvi por usar as wildcards e achei um recurso muito util: ``? extends Unit``

gerador de encontros: enquanto desenvolvia a mecanica dos dias e a geração de inimigos para cada batalha por dia, percebi que se apenas deixasse estáticos os encontros o jogo perderia replayability e ficaria "cru" resolvi isso adicionando encontros não aleatorios mas com chances diferentes pra conseguir diferenciar cada jogatina.




## 4. Diagrama de classes

<p align="center">
  <a href="wolfpackdiagramsimpler.png" target="_blank">
    <img src="wolfpackdiagramsimpler.png" width="1000" alt="Diagrama de Classes - WolfPack" title="Clique para ampliar">
  </a>
  <br>
  <em>Créditos: Plugin nativo Diagrams do Intellij IDEA Ultimate</em>
</p>

## 5. Orientacoes para execução

Pré-requisitos: JDK 17 ou superior e IntelliJ IDEA com suporte a Gradle.

Passo a passo:

1. Abra a pasta raiz LIBGDXProject no IntelliJ como um projeto Gradle e aguarde a sincronização das dependências.

2. Via Terminal: Na raiz do projeto, execute o comando de acordo com o sistema operacional:
Windows: gradlew.bat lwjgl3:run </br>
Linux / macOS: ./gradlew lwjgl3:run <br> Via IDE: Navegue pelo módulo lwjgl3, localize a classe Lwjgl3Launcher e execute o método main.

## 6. Resultado Final

![gif](wolfpackgif.gif)

## 7. Referências e créditos

LibGDX Framework: ferramenta open-source onde o projeto foi desenvolvido

Claude(Anthropic): utilizado como pair programming, ajudando na discussão de design e arquiteturas do sistema.

Gemini(Google): utilizado como assistente para geração do front-end, renderização de elementos gráficos para demonstração da lógica desenvolvida e para tweaks de valores para balanceamentos do jogo


input - forum: https://happycoding.io/tutorials/libgdx/input

Simple game - Libgdx forum: https://libgdx.com/wiki/start/simple-game-extended

Assets- Libgdx forum: https://libgdx.com/wiki/managing-your-assets

Viewport - Libgdx forum: https://libgdx.com/wiki/graphics/viewports

Network, para o backend e leaderboard - Libgdx forum: https://libgdx.com/wiki/networking

sprites, telas de combates: https://craftpix.net/product/clearing-in-the-forest-vector-battle-backgrounds/

sprites, inimigos: https://opengameart.org/content/animated-wild-animals

sprites, lobo: https://finalbossblues.itch.io/wolf-pack

sprites, icones: https://clockworkraven.itch.io/raven-fantasy-icons

tabela backend: https://docs.google.com/spreadsheets/d/1Dz5QWm4optUWYEsmMx_P-nFdY6pRRAHIahYo_ka5ivw/edit?gid=0#gid=0



abaixo prompts exemplo pra geração das telas(front-end):

1:"Based on the files ive sent you, i want you to make the ui, using the current already existent logic of draw and only integrating on it, based on this design and descriptions using a 1280x720:
on the top left wolves names above the hp bar of the respective wolf, and they stacked upon each other, in the top right same thing but with enemies, and make if they have statusEffects draw the icon, the logic is already on AssetLoader ready to use, in the 40% bottom of the screen divide by three sections, 40% for battle log, 30% for skill/target selection and 20% for description of the skill as its simple, and in the middle of the screen in the left side draw the wolves sprites and the enemies sprites, there cant be more than 3 enemies in the screen as by the logic i made so you can design it that way
DONT CHANGE NO-UI LOGIC"

2: "based on the current campscreen.java, i want you to adjust the campscreen based on what i already have made, make these options on the screen, option: feed(3 food cost, select one wolf and he heals 10 hp), trainAttack, trainDefense and trainHp, every wolf could train each one of this stats once only, and after selected display the wolves not dead and if they are already trained dont let them train, but dont waste it
also display the current day and the food the player has and include the background that its already on the assetLoader as campBackground, also use the iconOkay beside the name of the wolf to symbolize that it already has trained and dont let the player select but dont hide it too, and the player must select one of the four options to advance only options per day/camp"
