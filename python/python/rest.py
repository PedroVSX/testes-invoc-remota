from flask import Flask, jsonify
import json

app = Flask(__name__)

def carregar_dados():
    with open('t_6/dataset.json', 'r', encoding='utf-8') as f:
        return json.load(f)

data = carregar_dados()

@app.route('/usuarios', methods=['GET'])
def get_usuarios():
    return jsonify(data["usuarios"])

@app.route('/musicas', methods=['GET'])
def get_musicas():
    return jsonify(data["musicas"])

@app.route('/usuarios/<int:user_id>/playlists', methods=['GET'])
def get_playlists_usuario(user_id):
    playlists = [p for p in data["playlists"] if p["usuario_id"] == user_id]
    return jsonify(playlists)

@app.route('/playlists/<int:playlist_id>/musicas', methods=['GET'])
def get_musicas_playlist(playlist_id):
    playlist = next((p for p in data["playlists"] if p["id"] == playlist_id), None)
    musicas = [m for m in data["musicas"] if m["id"] in playlist["musicas"]] if playlist else []
    return jsonify(musicas)

@app.route('/musicas/<int:musica_id>/playlists', methods=['GET'])
def get_playlists_com_musica(musica_id):
    playlists = [p for p in data["playlists"] if musica_id in p["musicas"]]
    return jsonify(playlists)

if __name__ == '__main__':
    port = 5000
    print("Servidor Python Rest rodando")
    app.run(debug=True, port=port)