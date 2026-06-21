Java Event Planner
Projeto feito para disciplina de SCC0204 (Programação Orientada a Objetos). A ideia é um aplicativo de desktop em Java com Swing onde você consegue criar e gerenciar eventos num calendário mensal.

Integrantes
Nome	                  Número USP
Marcelo Palauro Morales	14594034
Rodrigo Eduardo Rubiano	16311091
Rapha Mendes	          XXXXXXX


O que o projeto faz
Basicamente é um gerenciador de eventos com calendário. Você clica numa data, vê os eventos daquele dia, e pode criar, editar ou excluir. Cada evento tem título, data, hora, local, descrição, categoria e um lembrete em horas antes do evento.
Também implementamos eventos recorrentes (diário, semanal ou mensal), que foi a parte mais trabalhosa do projeto. E na hora que abre o programa ele já mostra um popup com os eventos que estão chegando.
Funcionalidades
    • ☒ Calendário mensal com navegação entre meses
    • ☒ Datas com eventos são destacadas visualmente
    • ☒ Criar, editar e deletar eventos
    • ☒ Eventos recorrentes (diário, semanal, mensal) com data de fim opcional
    • ☒ Lembrete configurável por evento
    • ☒ Popup de lembretes na inicialização
    • ☒ Busca de eventos por palavra-chave
    • ☒ Dados salvos em CSV automaticamente
    • ☒ Validação dos campos com mensagens de erro

Como rodar
Só precisa do Java 11 ou superior, sem nenhuma dependência externa.
Pelo terminal
# compilar
javac -d out -sourcepath src src/eventplanner/Main.java

# executar
java -cp out eventplanner.Main
Pela IDE
Abre o projeto no NetBeans, IntelliJ ou Eclipse, define o src/ como pasta de fontes e roda a classe eventplanner.Main.
Na primeira vez que rodar, a pasta data/ é criada automaticamente pra guardar os eventos.

Estrutura de pastas
src/
└── eventplanner/
    ├── Main.java                     # entrada do programa
    ├── model/
    │   ├── Event.java                # classe do evento, com suporte a recorrência
    │   └── EventManager.java         # gerencia a lista e as queries de eventos
    ├── persistence/
    │   └── FileStorage.java          # salva e carrega o CSV
    └── view/
        ├── MainFrame.java            # janela principal
        ├── CalendarPanel.java        # o calendário em si
        ├── EventListPanel.java       # lista de eventos do dia clicado
        ├── EventDialog.java          # formulário de criar/editar evento
        └── ReminderDialog.java       # popup de lembretes

Sobre o arquivo de dados
Os eventos ficam em data/events.csv, separados por ;. O arquivo pode ter dois formatos — o antigo com 7 campos (sem recorrência) e o atual com 10 campos. Mantivemos compatibilidade com o formato antigo pra não quebrar arquivos salvos em versões anteriores do projeto.

Bug que a gente encontrou nos testes
Quando o usuário clicava numa data futura no calendário e abria o formulário de novo evento, o campo de data aparecia com a data de hoje ao invés da data que ele tinha clicado. Se não percebesse, o evento era salvo no dia errado.
O problema era que o EventDialog sempre inicializava o campo com LocalDate.now(). A correção foi passar a data selecionada do CalendarPanel pro EventDialog pelo construtor, pra ele já abrir com a data certa preenchida.

