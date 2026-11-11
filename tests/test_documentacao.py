"""Testes documentais independentes do Java; nao validam regras de negocio."""
import unittest
from pathlib import Path

RAIZ = Path(__file__).resolve().parents[1]

class TesteDocumentacao(unittest.TestCase):
    def test_arquivos(self):
        for nome in ("README.md", "AGENTS.md", "docs/REQUISITOS.md", "docs/ARQUITETURA.md", "docs/UML.puml", "docs/PLANO_TESTES.md"):
            with self.subTest(nome=nome):
                self.assertTrue((RAIZ / nome).is_file())

    def test_delimitadores_uml(self):
        uml = (RAIZ / "docs/UML.puml").read_text(encoding="utf-8")
        self.assertIn("@startuml", uml)
        self.assertIn("@enduml", uml)
