import asyncio
from datetime import datetime, timedelta
from aiogram import Bot
from dotenv import load_dotenv

import os
import sqlalchemy as sa
from sqlalchemy.ext.asyncio import create_async_engine

load_dotenv()

API_TOKEN = os.getenv('API_TOKEN')
boti = Bot(token=API_TOKEN)


async def async_main() -> None:
    engine = create_async_engine(
        "postgresql+asyncpg://postgres:11062002Andrey@postgres:5432/EcoCode", echo=True)

    async with engine.begin() as conn:
        table_users = await conn.run_sync(
            lambda conn: sa.Table(
                'users', sa.MetaData(), autoload_with=conn)
        )

        table_subs = await conn.run_sync(
            lambda conn: sa.Table(
                'subs', sa.MetaData(), autoload_with=conn)
        )

        table_services = await conn.run_sync(
            lambda conn: sa.Table(
                'services', sa.MetaData(), autoload_with=conn)
        )

        table_users_card = await conn.run_sync(
            lambda conn: sa.Table(
                'users_card', sa.MetaData(), autoload_with=conn)
        )

        table_tariff = await conn.run_sync(
            lambda conn: sa.Table(
                'tariff', sa.MetaData(), autoload_with=conn)
        )

    async with engine.connect() as conn:
        # select a Result, which will be delivered with buffered
        # results
        now = datetime.today()
        filter_after = datetime.today() + timedelta(days=3)
        subs = await conn.execute(sa.select(table_subs.c.user_id, table_subs.c.service_id, table_subs.c.tariff_id, table_subs.c.user_card_id, table_subs.c.date).where(table_subs.c.date < filter_after).where(table_subs.c.date > now))
        subs = subs.fetchall()
        for a in subs:
            tg_id = await conn.execute(sa.select(table_users.c.telegram_id).where(table_users.c.id == a[0]))
            tg_id = tg_id.fetchall()
            tg_id = tg_id[0][0]

            service = await conn.execute(sa.select(table_services.c.service_name).where(table_services.c.id == a[1]))
            service = service.fetchall()
            service = service[0][0]

            tariff = await conn.execute(sa.select(table_tariff.c.price).where(table_tariff.c.id == a[2]))
            tariff = tariff.fetchall()
            tariff = tariff[0][0]

            card = await conn.execute(sa.select(table_users_card.c.card_name).where(table_users_card.c.id == a[3]))
            card = card.fetchall()
            card = card[0][0]

            dt = datetime.now()
            date_only = dt.date()
            num_days = (a[4] - date_only).days

            if num_days == 1:
                await boti.send_message(tg_id, text=f'''
🛑 Внимание! 🛑
Завтра, {a[4].strftime("%d.%m.%Y")} с вашей карты {card} будет списана сумма {tariff} за подписку на {service}. Пожалуйста, убедитесь, что на счете достаточно средств!
    ''')
            else:
                await boti.send_message(tg_id, text=f'''
🛑 Внимание! 🛑
Через {num_days} дня {a[4].strftime("%d.%m.%Y")} с вашей карты {card} будет списана сумма {tariff} за подписку на {service}. Пожалуйста, убедитесь, что на счете достаточно средств!
    ''')

    # for AsyncEngine created in function scope, close and
    # clean-up pooled connections
    await engine.dispose()
