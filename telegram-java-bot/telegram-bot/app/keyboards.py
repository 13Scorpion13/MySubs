from aiogram.types import ReplyKeyboardMarkup, KeyboardButton

get_number = ReplyKeyboardMarkup(keyboard=[[KeyboardButton(text='Отправить номер', request_contact=True)]])