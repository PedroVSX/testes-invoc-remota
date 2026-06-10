#!/bin/bash

# Uso: ./executar_testes.sh [LINGUAGEM] [API]
# Exemplo: ./executar_testes.sh java rest

# Obtém o diretório onde o script está localizado
DIR="$(cd "$(dirname "$0")" && pwd)"

LINGUAGEM=$1
API=$2
HOST_BASE="http://localhost:5000"
GRPC_HOST="localhost:5000"   # porta do grpc-server-spring-boot-starter
# Caminho absoluto para o arquivo locust para evitar erros de diretório
LOCUST_FILE="${DIR}/locustfile_comportamento.py"
TIME="2m"

if [ -z "$LINGUAGEM" ] || [ -z "$API" ]; then
    echo "Erro: Forneça a linguagem e a API."
    echo "Uso: $0 [java|python] [rest|graphql|soap|grpc]"
    exit 1
fi

if [ ! -f "$LOCUST_FILE" ]; then
    echo "Erro: Arquivo não encontrado em $LOCUST_FILE"
    exit 1
fi

# Mapeia a API para a Classe do Locust
case $API in
    rest) CLASS="RestUser";;
    graphql) CLASS="GraphqlUser";;
    soap) CLASS="SoapUser";;
    grpc) CLASS="GrpcUser";;
    *) echo "API desconhecida: $API"; exit 1;;
esac

# Tenta usar a pasta results na raiz do projeto se possível, ou no dir atual
OUTPUT_DIR="results"
mkdir -p $OUTPUT_DIR

echo ">>> Iniciando Testes: Linguagem=$LINGUAGEM | API=$API | Host=$HOST_BASE"

for CENARIO in "leve 1000 100" "medio 2000 200" "pesado 4000 400"; do
    NIVEL=$(echo $CENARIO | cut -d' ' -f1)
    USERS=$(echo $CENARIO | cut -d' ' -f2)
    SPAWN=$(echo $CENARIO | cut -d' ' -f3)
    
    FILE_PREFIX="${OUTPUT_DIR}/${LINGUAGEM}_${API}_${NIVEL}"
    
    echo "----------------------------------------------------"
    echo "Executando cenário: $NIVEL ($USERS users, $SPAWN spawn_rate)"
    echo "Salvando em: ${FILE_PREFIX}_stats.csv"
    echo "----------------------------------------------------"

    # Para o gRPC, usa host:porta dedicado (sem prefixo http://)
    if [ "$API" = "grpc" ]; then
        CURRENT_HOST="$GRPC_HOST"
    else
        CURRENT_HOST="$HOST_BASE"
    fi

    locust -f "$LOCUST_FILE" "$CLASS" \
        --headless \
        -u $USERS \
        -r $SPAWN \
        -t $TIME \
        --host "$CURRENT_HOST" \
        --csv "$FILE_PREFIX" \
        --only-summary

    echo "Cenário $NIVEL finalizado."
    sleep 5
done

echo ">>> Todos os cenários para ${LINGUAGEM}/${API} concluídos."
