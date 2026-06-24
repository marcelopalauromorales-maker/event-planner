# Java Event Planner

Projeto feito para disciplina de SCC0204 (Programação Orientada a Objetos). A ideia é um aplicativo de desktop em Java com Swing onde você consegue criar e gerenciar eventos num calendário mensal.

---

## Integrantes

| Nome | Número USP |
|------|------------|
| Marcelo Palauro Morales | 14594034 |
| Rodrigo Eduardo Rubiano | 16311091 |
| Rapha Mendes | 15497660 |

---

## O que o projeto faz

Basicamente é um gerenciador de eventos com calendário. Você clica numa data, vê os eventos daquele dia, e pode criar, editar ou excluir. Cada evento tem título, data, hora, local, descrição, categoria e um lembrete em horas antes do evento.

Também implementamos eventos recorrentes (diário, semanal ou mensal), que foi a parte mais trabalhosa do projeto. E na hora que abre o programa ele já mostra um popup com os eventos que estão chegando.

---

## Funcionalidades

### Calendário
O calendário mostra o mês inteiro e destaca os dias que tem evento com uma cor de fundo baseada na categoria do primeiro evento daquele dia (reunião em vermelho claro, aniversário em verde, estudo em amarelo e assim por diante). Passando o mouse em cima de um dia aparece um tooltip com os títulos dos eventos. Tem botões pra navegar entre meses e um botão "Today" que volta direto pro dia de hoje.

### Eventos
Dá pra criar, editar e deletar eventos. Cada evento guarda título, data, horário, local, descrição, categoria e quantas horas antes o lembrete deve aparecer. As categorias disponíveis são: Meeting, Birthday, Appointment, Study, Leisure e Other.

### Eventos recorrentes
Na criação do evento tem uma opção pra marcar como recorrente e escolher se repete diário, semanal ou mensal. Também dá pra colocar uma data de fim, ou deixar em branco pra repetir sem prazo. Ao deletar um evento recorrente o programa pergunta se quer apagar só aquela ocorrência ou a série inteira.

### Lembretes
Cada evento tem um campo de lembrete em horas (de 0 a 168h antes). Quando o programa é aberto ele verifica automaticamente quais eventos estão dentro do prazo de lembrete das próximas 24 horas e mostra um popup com a lista deles.

### Busca
Tem uma barra de busca no topo da janela. A pesquisa olha pro título e pra descrição dos eventos e mostra os resultados na lista lateral.

### Persistência
Os eventos são salvos automaticamente num arquivo CSV sempre que tem alguma alteração. Na próxima vez que abrir o programa os dados são carregados de volta. Se o arquivo não existir ainda, o programa cria a pasta `data/` sozinho na primeira execução.

### Validação e erros
Campos obrigatórios (título, data e horário) são validados antes de salvar. Formatos de data ou horário errados mostram uma mensagem de erro clara, sem expor stack traces pro usuário.

---

## Como rodar

Só precisa do Java 11 ou superior, sem nenhuma dependência externa.

### Pelo terminal

```bash
# compilar
javac -d out -sourcepath src src/eventplanner/Main.java

# executar
java -cp out eventplanner.Main
```

### Pela IDE

Abre o projeto no NetBeans, IntelliJ ou Eclipse, define o `src/` como pasta de fontes e roda a classe `eventplanner.Main`.

> Na primeira vez que rodar, a pasta `data/` é criada automaticamente pra guardar os eventos.

---

## Estrutura de pastas

```
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
```

---

## Sobre o arquivo de dados

Os eventos ficam em `data/events.csv`, separados por `;`. O arquivo pode ter dois formatos — o antigo com 7 campos (sem recorrência) e o atual com 10 campos. Mantivemos compatibilidade com o formato antigo pra não quebrar arquivos salvos em versões anteriores do projeto.

---

## Bug que a gente encontrou nos testes

Quando o usuário clicava numa data futura no calendário e abria o formulário de novo evento, o campo de data aparecia com a data de hoje ao invés da data que ele tinha clicado. Se não percebesse, o evento era salvo no dia errado.

O problema era que o `EventDialog` sempre inicializava o campo com `LocalDate.now()`. A correção foi passar a data selecionada do `CalendarPanel` pro `EventDialog` pelo construtor, pra ele já abrir com a data certa preenchida.
