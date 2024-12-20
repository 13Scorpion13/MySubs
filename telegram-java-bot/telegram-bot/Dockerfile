FROM python:3.10-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --upgrade pip
RUN pip install --no-cache-dir -r requirements.txt

COPY .env .env
COPY . .

EXPOSE 8080

CMD ["python", "main.py"]